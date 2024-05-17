package com.concurrency.ecommerce.integration;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.application.*;
import com.concurrency.ecommerce.user.domain.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserPointConcurrencyTest {

    @Autowired
    UserPointFacade userPointFacade;

    @Autowired
    UserPointService userPointService;

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - ExecutorService 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 선착순 1건만 성공하고 나머지 요청은 무시한다.")
    public void optimisticLockTest() throws InterruptedException {
        //given
        int core = 10;
        ExecutorService executor = Executors.newFixedThreadPool(core);
        CountDownLatch latch = new CountDownLatch(core);
        UserPointParam param = new UserPointParam(1L, BigInteger.valueOf(10000));

        //when
        for (int i = 0; i < core; i++) {
            executor.submit(() -> {
                try {
                    UserPointParam charge = userPointFacade.charge(param);
                    System.out.println("[충전 성공] " + charge.getPoint() + " | " + Thread.currentThread().getName());
                } catch (EcommerceException e) {
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
        assertThat(afterPoint.getPoint()).isEqualTo(BigInteger.valueOf(20000));
    }

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - CompletableFuture 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 선착순 1건만 성공하고 나머지 요청은 무시한다.")
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
}

    //    @Test
//    @DisplayName("분산락 적용해서 동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 테스트.")
//    public void useRedissonCharge() throws ExecutionException, InterruptedException {
//        //given
//        Long userId = 1L;
//
//        UserPointResponse originUserPoint = UserPointResponse.of(userPointService.getPoint(userId));
//        BigInteger chargePoint = BigInteger.valueOf(10000);
//        UserPointRequest request = new UserPointRequest(userId, chargePoint);
//
//        //when
//        CompletableFuture<UserPointResponse> charge = CompletableFuture.supplyAsync(() -> redissonLockUserPointFacade.executeCharge(request));
//        List<CompletableFuture<UserPointResponse>> futureList = IntStream.range(0, 10)
//                .mapToObj(i -> charge)
//                .collect(Collectors.toList());
//
//        CompletableFuture<List<UserPointResponse>> allDoneFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
//                .thenApply(v -> futureList.stream()
//                        .map(CompletableFuture::join)
//                        .collect(Collectors.toList()));
//        allDoneFuture.get().forEach(response -> System.out.println(response.getPoint()));
//
//        UserPointResponse result = optimisticLockUserPointFacade.point(1L);
//
//        //then
//        assertThat(result.getPoint()).isEqualTo(BigInteger.valueOf(20000));
//        assertThat(result.getPoint()).isEqualTo(originUserPoint.getPoint().add(chargePoint));
//    }

