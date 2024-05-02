package com.concurrency.demo.integration;

import com.concurrency.demo.domain.ConcurrentHashMapProductService;
import com.concurrency.demo.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConcurrentHashMapProductServiceTest {

    @Autowired
    ConcurrentHashMapProductService service;

    /*
     * 비동기 테스트 [674ms]
     */
    @Test
    void decreaseStock() {
        //given
        Long id = 1L;
        Long quantity = 1L;
        Product requestProduct = new Product(id, quantity);
        int count = 100;
        AtomicInteger exceptionCount = new AtomicInteger(0);    // 재고 차감 예외 카운트
        List<CompletableFuture<Void>> futures = new ArrayList<>();        // 비동기 작업 목록

        //when
        Long startTimeConcurrentLock = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {       // supplyAsync() : 비동기 작업을 생성
                try { service.decreaseStock(requestProduct); }
                catch (Exception e) { exceptionCount.addAndGet(1); }
                return null;
            }));
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));    // 모든 비동기 작업 리스트를 하나의 CompletableFuture 객체로 생성
        allFutures.join();
        Long endTimeConcurrentLock = System.currentTimeMillis();
        System.out.println("걸린 시간 : " + (endTimeConcurrentLock - startTimeConcurrentLock) + " ms");
        //then
        assertThat(service.findProduct(id).getStock()).isEqualTo(0 + exceptionCount.get()) ;
    }
}