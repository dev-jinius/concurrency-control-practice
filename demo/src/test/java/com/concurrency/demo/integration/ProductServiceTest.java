package com.concurrency.demo.integration;

import com.concurrency.demo.domain.Product;
import com.concurrency.demo.domain.ProductService;
import com.concurrency.demo.domain.SynchronizedProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 동시성 이슈 발생 테스트
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceTest {

    @Autowired
    ProductService service;

    /*
      [동시성 이슈]
      - 조건 : 재고가 100개인 상품의 재고를 멀티 스레드 환경(총 스레드 10개)에서 수량 1개 차감하는 요청을 동시에 100번 요청한다.
      - race condition 발생하는 상황
        1. 스레드 A가 상품 조회를 하고(재고 100), 재고 차감을 한다. 아직 DB 반영 전.
        2. 스레드 B가 상품 조회를 하고(재고 100), 재고 차감을 한 후 DB에 반영이 된다. => 재고 99
        3. 스레드 A가 재고 차감한 것을 DB에 반영한다. => 저장한 값이 무시될 수 있다. 재고 99
      */
    @Test
    @DisplayName("Condition Race을 강제로 발생시켜, 100개의 재고가 있는 상품에 동시에 100번의 재고 차감을 했을 때, 재고가 모두 차감되지 않는다.")
    void non_concurrency_decrease_stock() throws InterruptedException {
        //given
        Long id = 1L;
        Long quantity = 1L;
        Product requestProduct = new Product(id, quantity);
        int core = 10;
        int count = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CountDownLatch countDownLatch = new CountDownLatch(count);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName());
                try {
                    System.out.println(this.getClass().getName());
                    service.decreaseStock(requestProduct);
                } catch (Exception e) {
                    exceptionCount.addAndGet(1);
                }
                finally {
                    System.out.println(Thread.currentThread().getName());
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        Long result = service.findProduct(id).getStock();

        //then
//        assertThat(result).isEqualTo(0L);   // 일부러 실패하는 Case => 남은 재고 확인 가능
        assertThat(result).isNotEqualTo(0L + exceptionCount.get());
        assertThat(result).isGreaterThan(0L);
    }
}