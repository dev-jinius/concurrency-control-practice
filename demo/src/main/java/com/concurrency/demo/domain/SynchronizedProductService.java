package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SynchronizedProductService {

    private final ProductRepository productRepository;

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * Synchronized 적용한 재고 차감 동시성 처리
     * @param requestProduct
     * @throws Exception
     */
    public synchronized void decreaseStock(Product requestProduct) throws Exception {
        // 재고 차감
        Product product = findProduct(requestProduct.getProductId());
        product.decreaseStock(requestProduct.getStock());

        productRepository.saveAndFlush(product);
    }
}
