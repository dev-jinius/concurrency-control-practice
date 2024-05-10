package com.concurrency.ecommerce.product.application;

import com.concurrency.ecommerce.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
}
