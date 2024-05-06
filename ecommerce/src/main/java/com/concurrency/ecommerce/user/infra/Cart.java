package com.concurrency.ecommerce.user.infra;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 장바구니
 */
@Entity
@Table(name = "tb_cart")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private Long userId;

    private Long itemId;

    private Long quantity;

    @CreatedDate
    private LocalDateTime createDate;
}
