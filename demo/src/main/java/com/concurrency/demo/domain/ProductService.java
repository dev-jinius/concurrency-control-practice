package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * 동시성 제어 없는 재고 차감
     * @param requestProduct
     */
    public Product decreaseStock(Product requestProduct) throws Exception {
        Product product = findProduct(requestProduct.getProductId());
        product.decreaseStock(requestProduct.getStock());

        return productRepository.saveAndFlush(product);
    }
}
