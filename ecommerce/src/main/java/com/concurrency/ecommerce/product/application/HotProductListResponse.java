package com.concurrency.ecommerce.product.application;

import lombok.*;

import java.math.BigInteger;

/**
 * 인기 상품 목록 응답
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HotProductListResponse {

    private Long productId;             // 상품 ID
    private String productName;         // 상품명
    private BigInteger productPrice;    // 상품 가격
    private Long stockQuantity;         // 재고 수량
    private Long salesQuantity;         // 판매 수량 (최근 3일)
}
