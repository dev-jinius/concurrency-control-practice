package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReentrantLockProductService {

    private final ProductRepository productRepository;

    private final ReentrantLock lock = new ReentrantLock();

    private static int count = 0;
    private static int failCount = 0;

    @Transactional
    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * 명시적으로 Lock 사용한 재고 차감 동시성 처리
     * @param requestProduct
     * @throws Exception
     */
    @Transactional
    public Product decreaseStock(Product requestProduct) throws Exception {
        if(lock.isLocked()) {
            failCount += 1;
            log.error("[{}] 락 획득 실패 [{}]", failCount, Thread.currentThread().getName());
            throw new Exception("락 획득 실패");
        }

        lock.lock();    // 락 획득
        count +=1;
        try {
            log.info("[{}] 락 걸림 [{}]", count, Thread.currentThread().getName());
            Product product = findProduct(requestProduct.getProductId());
            product.decreaseStock(requestProduct.getStock());
            return productRepository.saveAndFlush(product);
        } finally {
            lock.unlock(); // 락 해제
            log.info("락 해제 [{}]", Thread.currentThread().getName());
        }
    }
}
