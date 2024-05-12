package com.concurrency.demo.integration;

import com.concurrency.demo.domain.PessimisticLockProductService;
import com.concurrency.demo.domain.Product;
import com.concurrency.demo.domain.RequestProduct;
import com.concurrency.demo.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PessimisticLockProductServiceTest {

    @Autowired
    PessimisticLockProductService service;

    @Autowired
    ProductRepository productRepository;

    @Test
    @Transactional
    @DisplayName("비관적 락을 사용하여 재고가 100개 있는 상품 4개에 대한 동시에 100번의 재고 조회와 각 상품에 대해 1개 재고 수량 차감 요청이 있는 경우, 재고가 0이 되도록 하고, 동시성을 보장한다.")
    void decreaseStock() throws InterruptedException {
        //given
        int core = 10;
        int count = 100;
        List<RequestProduct> requestProducts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            requestProducts.add(new RequestProduct((long) (i+1), 1L));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CountDownLatch countDownLatch = new CountDownLatch(count);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName());
                try {
                    System.out.println(this.getClass().getName());
                    service.decreaseStock(requestProducts);
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

        List<Product> resultList = productRepository.findByProductIdInOrderByProductId(requestProducts.stream().map(p -> p.getProductId()).toList());

        //then
        for (Product product : resultList) {
            assertThat(product.getStock()).isEqualTo(0L + exceptionCount.get());
        }
    }
}