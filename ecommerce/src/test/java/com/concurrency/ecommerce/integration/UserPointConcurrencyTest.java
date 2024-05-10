package com.concurrency.ecommerce.integration;

import com.concurrency.ecommerce.user.application.UserPointFacade;
import com.concurrency.ecommerce.user.application.UserPointRequest;
import com.concurrency.ecommerce.user.application.UserPointResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserPointConcurrencyTest {

    @Autowired
    private UserPointFacade userPointFacade;

    /**
     * 낙관적 락 이용한 동일 유저 포인트 충전 동시성 테스트
     */
    @Test
    @DisplayName("동시에 같은 유저에 대해 10번의 충전 요청이 발생한 경우, 선착순 1건만 성공하고 나머지 요청은 무시한다.")
    public void concurrencyCharge() throws ExecutionException, InterruptedException {
        //given
        Long userId = 1L;
        UserPointResponse originUserPoint = userPointFacade.point(userId);
        BigInteger chargePoint = BigInteger.valueOf(10000);
        UserPointRequest request = new UserPointRequest(userId, chargePoint);

        //when
        CompletableFuture<UserPointResponse> charge = CompletableFuture.supplyAsync(() -> userPointFacade.charge(request));
        List<CompletableFuture<UserPointResponse>> futureList = IntStream.range(0, 10)
                .mapToObj(i -> charge)
                .collect(Collectors.toList());

        CompletableFuture<List<UserPointResponse>> allDoneFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
                        .thenApply(v -> futureList.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));
        allDoneFuture.get().forEach(response -> System.out.println(response.getPoint()));

        UserPointResponse result = userPointFacade.point(1L);

        //then
        assertThat(result.getPoint()).isEqualTo(BigInteger.valueOf(20000));
        assertThat(result.getPoint()).isEqualTo(originUserPoint.getPoint().add(chargePoint));
    }
}
