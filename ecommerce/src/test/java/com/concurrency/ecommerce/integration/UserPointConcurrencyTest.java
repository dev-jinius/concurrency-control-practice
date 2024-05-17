package com.concurrency.ecommerce.integration;

import com.concurrency.ecommerce.RedissonConfig;
import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.user.application.*;
import com.concurrency.ecommerce.user.domain.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(RedissonConfig.class)
public class UserPointConcurrencyTest {

    @Autowired
    UserPointFacade userPointFacade;

    @Autowired
    UserPointService userPointService;

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - ExecutorService 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 실패 시 요청을 무시하고 성공한 요청만 DB에 update한다.")
    public void optimisticLockTest() throws InterruptedException {
        //given
        int core = 10;
        ExecutorService executor = Executors.newFixedThreadPool(core);
        CountDownLatch latch = new CountDownLatch(core);
        UserPointParam beforeUser = userPointFacade.point(1L);
        UserPointParam param = new UserPointParam(1L, BigInteger.valueOf(10000));

        AtomicInteger success = new AtomicInteger(core);
        //when
        for (int i = 0; i < core; i++) {
            executor.submit(() -> {
                try {
                    UserPointParam charge = userPointFacade.charge(param);
                    System.out.println("[충전 성공] " + charge.getPoint() + " | " + Thread.currentThread().getName());
                } catch (EcommerceException e) {
                    success.addAndGet(-1);
                    System.out.println("[낙관적 락] " + e.getErrorCode().getMessage() + " | " + Thread.currentThread().getName());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        //then
        UserPointParam afterPoint = userPointFacade.point(1L);
        assertThat(afterPoint.getPoint()).isEqualTo(beforeUser.getPoint().add(BigInteger.valueOf(10000).multiply(BigInteger.valueOf(success.intValue()))));
    }

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - CompletableFuture 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 실패 시 요청을 무시하고 성공한 요청만 DB에 update한다.")
    public void optimisticLockTest2() throws ExecutionException, InterruptedException {
        //given
        Long userId = 1L;
        UserPointParam beforeUser = userPointFacade.point(userId);
        BigInteger chargePoint = BigInteger.valueOf(10000);
        UserPointParam request = new UserPointParam(userId, chargePoint);

        //when
        CompletableFuture<UserPointParam> charge = CompletableFuture.supplyAsync(() -> userPointFacade.charge(request));

        List<CompletableFuture<UserPointParam>> futureList = IntStream.range(0, 10)
                .mapToObj(i -> charge)
                .collect(Collectors.toList());

        CompletableFuture<List<UserPointParam>> allDoneFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
                .thenApply(v -> futureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        allDoneFuture.get();

        //then
        UserPointParam result = userPointFacade.point(1L);
        assertThat(result.getPoint()).isEqualTo(BigInteger.valueOf(20000));
        assertThat(result.getPoint()).isEqualTo(beforeUser.getPoint().add(chargePoint));
    }

    @Test
    @DisplayName("분산락 적용해서 동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 실패 시 요청을 무시하고 성공한 요청만 DB에 update한다.")
    public void useRedissonCharge() throws InterruptedException {
        //given
        int core = 10;
        ExecutorService executor = Executors.newFixedThreadPool(core);
        CountDownLatch latch = new CountDownLatch(core);

        UserPointParam beforeUser = UserPointParam.of(userPointService.point(1L));
        BigInteger chargePoint = BigInteger.valueOf(10000);
        UserPointParam param = new UserPointParam(1L, chargePoint);

        AtomicInteger success = new AtomicInteger(core);
        //when
        for (int i = 0; i < core; i++) {
            executor.submit(() -> {
                try {
                    UserPointParam charge = userPointFacade.executeCharge(param);
                    System.out.println("[충전 성공] " + charge.getPoint() + " | " + Thread.currentThread().getName());
                } catch (EcommerceException e) {
                    success.addAndGet(-1);
                    System.out.println("[락 걸림] " + e.getErrorCode().getMessage() + " | " + Thread.currentThread().getName());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        //then
        UserPointParam afterPoint = userPointFacade.point(1L);
        assertThat(afterPoint.getPoint()).isEqualTo(beforeUser.getPoint().add(BigInteger.valueOf(10000).multiply(BigInteger.valueOf(success.intValue()))));
    }
}