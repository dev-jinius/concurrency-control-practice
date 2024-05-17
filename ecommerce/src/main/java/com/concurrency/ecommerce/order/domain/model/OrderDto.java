package com.concurrency.ecommerce.order.domain.model;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OrderDto {

    private Long orderId;               // 주문 ID
    private String userId;              // 유저 ID
    private BigInteger orderPrice;      // 주문 총 금액
    private LocalDateTime createAt;     // 주문 시간
}
