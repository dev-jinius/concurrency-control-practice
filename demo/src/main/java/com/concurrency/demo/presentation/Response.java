package com.concurrency.demo.presentation;

import com.concurrency.demo.domain.Product;
import lombok.*;

/**
 * 응답 객체
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Response {

    private Long productId;         // 상품 ID
    private Long stock;             // 재고 수량

    public static Response of(Product product) {
        return Response.builder()
                .productId(product.getProductId())
                .stock(product.getStock())
                .build();
    }
}
