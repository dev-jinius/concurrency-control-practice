package com.concurrency.ecommerce.integration;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.user.application.OptimisticLockUserPointFacade;
import com.concurrency.ecommerce.user.application.UserPointRequest;
import com.concurrency.ecommerce.user.application.UserPointResponse;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserPointConcurrencyTest {

    @Autowired
    OptimisticLockUserPointFacade optimisticLockUserPointFacade;

    @Autowired
    UserPointService userPointService;

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - ExecutorService 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 선착순 1건만 성공하고 나머지 요청은 무시한다.")
    public void optimisticLockUserPointCharge() throws InterruptedException {
        //given
        int core = 10;
        ExecutorService executor = Executors.newFixedThreadPool(core);
        CountDownLatch latch = new CountDownLatch(core);
        UserPointRequest request = new UserPointRequest(1L, BigInteger.valueOf(10000));

        //when
        for (int i = 0; i < core; i++) {
            executor.submit(() -> {
                try {
                    optimisticLockUserPointFacade.charge(request);
                } catch (EcommerceException e) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        //then
        UserPointDto afterCharge = userPointService.getPoint(1L);
        assertThat(afterCharge.getPoint()).isEqualTo(BigInteger.valueOf(20000));
    }

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트 - CompletableFuture 사용
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 선착순 1건만 성공하고 나머지 요청은 무시한다.")
    public void concurrencyCharge() throws ExecutionException, InterruptedException {
        //given
        Long userId = 1L;
        UserPointResponse userPointResponse = optimisticLockUserPointFacade.point(userId);
        BigInteger chargePoint = BigInteger.valueOf(10000);
        UserPointRequest request = new UserPointRequest(userId, chargePoint);

        //when
        CompletableFuture<UserPointResponse> charge = CompletableFuture.supplyAsync(() -> optimisticLockUserPointFacade.charge(request));

        List<CompletableFuture<UserPointResponse>> futureList = IntStream.range(0, 10)
                .mapToObj(i -> charge)
                .collect(Collectors.toList());

        CompletableFuture<List<UserPointResponse>> allDoneFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
                        .thenApply(v -> futureList.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));

        allDoneFuture.get().forEach(response -> {
            if (!ObjectUtils.isEmpty(response))
                System.out.println(response.getPoint());
        });

        //then
        UserPointResponse result = optimisticLockUserPointFacade.point(1L);
        assertThat(result.getPoint()).isEqualTo(BigInteger.valueOf(20000));
        assertThat(result.getPoint()).isEqualTo(userPointResponse.getPoint().add(chargePoint));
    }
}
