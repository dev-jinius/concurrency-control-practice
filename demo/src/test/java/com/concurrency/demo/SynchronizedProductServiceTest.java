package com.concurrency.demo;

import com.concurrency.demo.domain.Product;
import com.concurrency.demo.domain.SynchronizedProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Synchronized 키워드를 사용한 재고 차감 동시성 제어 테스트
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SynchronizedProductServiceTest {

    @Autowired
    SynchronizedProductService service;

    /*
        명시적 스레드 동시성 테스트
        [Synchronized로 동시성 제어]
        - 조건 : 단일 프로세스 내에서 테스트 메서드에 synchronized를 적용해 Application Level에서 Lock을 적용
     */
    @Test
    @DisplayName("synchronzied를 사용하여 100개의 재고가 있는 상품에 동시에 100번의 재고 차감을 했을 때, 동시성을 보장하여 남은 재고가 0이 된다.")
    void concurrency_decrease_stock_with_executorService() throws InterruptedException {
        //given
        Long id = 1L;
        Long quantity = 1L;
        Product requestProduct = new Product(id, quantity);
        int core = 10;
        int count = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CountDownLatch countDownLatch = new CountDownLatch(count);
        AtomicInteger exceptionCount = new AtomicInteger(0);    // 재고 차감 예외 카운트

        //when
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                try {
                    service.decreaseStock(requestProduct);
                } catch (Exception e) {
                    exceptionCount.addAndGet(1);
                }
                finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        //then
        assertThat(service.findProduct(id).getStock()).isEqualTo(0 + exceptionCount.get()) ;
    }

    /*
        비동기 동시성 테스트
     */
    @Test
    @DisplayName("synchronzied를 사용하여 100개의 재고가 있는 상품에 동시에 100번의 재고 차감을 했을 때, 동시성을 보장하여 남은 재고가 0이 된다.")
    void concurrency_decrease_stock_with_completableFuture() {
        //given
        Long id = 1L;
        Long quantity = 1L;
        Product requestProduct = new Product(id, quantity);
        int count = 100;
        AtomicInteger exceptionCount = new AtomicInteger(0);    // 재고 차감 예외 카운트
 
        //when
        List<CompletableFuture<Void>> futures = new ArrayList<>();  // 비동기 작업 목록
        for (int i = 0; i < count; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {       // supplyAsync() : 비동기 작업을 생성
                try { service.decreaseStock(requestProduct); }
                catch (Exception e) { exceptionCount.addAndGet(1); }
                return null;
            }));
        }

        //allOf() : 새로운 CompletableFuture 객체를 생성한다.
        //futures.toArray() : futures를 CompletableFuture 객체의 배열로 변환한다.
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));    // 모든 비동기 작업 리스트를 하나의 CompletableFuture 객체로 생성
        //allFutures의 모든 비동기 작업이 실행될 떄까지 현재 스레드를 차단한다.
        allFutures.join();

        //then
        assertThat(service.findProduct(id).getStock()).isEqualTo(0 + exceptionCount.get()) ;
    }
}