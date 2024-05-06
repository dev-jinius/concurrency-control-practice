package com.concurrency.ecommerce.order.application;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 요청한 주문 상품 정보
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemRequest {

    private Long productId;         // 상품 ID
    private Long quantity;          // 수량
}
