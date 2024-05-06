package com.concurrency.ecommerce.order.infra;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_orders")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Order {

    /**
     * 주문 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    /**
     * 유저 정보
     */
    private Long userId;

    /**
     * 주문 상태 [CANCLE, DONE]
     */
    private String orderStatus;

    /**
     * 총 주문 가격
     */
    @Column(columnDefinition = "BIGINT")
    private BigInteger orderPrice;

    /**
     * 주문 시간
     */
    @CreatedDate
    private LocalDateTime orderedAt;
}