package com.concurrency.ecommerce.order.application;

import com.concurrency.ecommerce.order.infra.OrderItem;
import lombok.*;

import java.util.List;

/**
 * 주문 요청
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderRequest {

    private Long userId;                                //유저 ID
    private List<OrderItemRequest> orderItemList;       //주문 상품 목록
}
