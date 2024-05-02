package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrentHashMapProductService {

    private final ProductRepository productRepository;

    //ConcurrentHashMap: 여러 스레드가 동시에 접근하더라도 데이터에 일관성을 유지한다.
    //productId와 ReentrantLock을 키와 값으로 저장하며, productId에 대해 1개의 ReentrantLock 객체만 존재하도록 관리한다.
    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * ConcurrentHashMap과 ReentrantLock 사용한 재고 차감 동시성 처리
     * @param requestProduct
     * @throws Exception
     */
    @Transactional
    public Product decreaseStock(Product requestProduct) throws Exception {
        ReentrantLock lock = locks.putIfAbsent(requestProduct.getProductId(), new ReentrantLock());

        if(lock.isLocked()) {
            log.error("[{}] 락 획득 실패", Thread.currentThread().getName());
            throw new Exception("락 획득 실패");
        }

        lock.lock();
        try {
            log.info("[{}] 락 획득 성공", Thread.currentThread().getName());
            Product product = findProduct(requestProduct.getProductId());
            product.decreaseStock(requestProduct.getStock());
            return productRepository.saveAndFlush(product);
        } finally {
            lock.unlock();
        }

        // 요청한 상품 ID가 map에 없다면, lock 생성해서 저장한다.
        // 요청한 상품 ID가 map에 있다면, 기존 ReentrantLock 객체를 반환한다.
        // computeIfAbsent() : productId에 대한 ReentrantLock 객체를 반환하거나 생성한다.
//        ReentrantLock lock = locks.computeIfAbsent(requestProduct.getProductId(), (key) -> new ReentrantLock());
        // 3초동안 Lock 획득 시도 -> true => 재고 차감 / false => Exception 처리
//        boolean isAcquiredLock = lock.tryLock(5, TimeUnit.SECONDS);
//        try {
//            if (isAcquiredLock) {
//                Product product = findProduct(requestProduct.getProductId());
//                product.decreaseStock(requestProduct.getStock());
//                return productRepository.saveAndFlush(product);
//            } else {
//                throw new Exception("TIME OUT");
//            }
//        } finally {
//            lock.unlock();
//        }
    }
}
