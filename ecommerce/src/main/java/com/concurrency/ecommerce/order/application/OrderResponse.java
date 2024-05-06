package com.concurrency.ecommerce.order.application;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 주문 응답
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long userId;                            //유저 ID
    private BigInteger orderPrice;                  //총 주문 금액
    private BigInteger point;                       //결제 후 포인트
    private LocalDateTime createDate;               //주문 일시
}
