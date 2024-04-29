package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ReentrantLockProductService {

    private final ProductRepository productRepository;

    private final ReentrantLock lock = new ReentrantLock();

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * 명시적으로 Lock 사용한 재고 차감 동시성 처리
     * @param requestProduct
     * @throws Exception
     */
    public void decreaseStock(Product requestProduct) throws Exception {

        lock.lock();    // 락 획득
        try {
            Product product = findProduct(requestProduct.getProductId());
            product.decreaseStock(requestProduct.getStock());
            productRepository.saveAndFlush(product);
        } finally {
            lock.unlock(); // 락 해제
        }
    }
}
