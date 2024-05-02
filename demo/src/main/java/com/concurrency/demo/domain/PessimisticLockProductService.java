package com.concurrency.demo.domain;

import com.concurrency.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PessimisticLockProductService {

    private final ProductRepository productRepository;

    public Product findProduct(Long id) {
        return productRepository.findById(id).get();
    }

    @Transactional
    public List<Product> decreaseStock(List<RequestProduct> requestProducts) throws Exception {
        List<Long> productList = requestProducts.stream().map(p -> p.getProductId()).toList();   //Arrays.asList(1L, 2L, 3L);
        List<Product> findProducts = productRepository.findByProductIdInOrderByProductId(productList);
        List<Product> resultProducts = new ArrayList<>();

        for (RequestProduct product : requestProducts) {
            Product decreasedProduct = findProducts.stream()
                    .filter(p -> p.getProductId().equals(product.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(product.getProductId() + " 상품 정보가 없습니다."));
            //재고 차감
            decreasedProduct.decreaseStock(product.getQuantity());
            resultProducts.add(decreasedProduct);
            //재고 저장
            productRepository.saveAndFlush(decreasedProduct);
        }

        return resultProducts;
    }
}
