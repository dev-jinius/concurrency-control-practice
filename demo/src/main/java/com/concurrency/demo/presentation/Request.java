package com.concurrency.demo.presentation;

import com.concurrency.demo.domain.Product;
import lombok.*;

/**
 * 재고 차감 요청 객체
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Request {

    private Long productId;         // 상품 ID
    private Long quantity;         // 주문 수량

    public Product toDomain() {
        return Product.builder()
                .productId(productId)
                .stock(quantity)
                .build();
    }
}
