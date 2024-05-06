package com.concurrency.ecommerce.product.infra;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "tb_product")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false, length = 50)
    private String name;

    @Column(name = "product_price", nullable = false, columnDefinition = "BIGINT")
    private BigInteger price;

    @Column(name = "stock_quantity", nullable = false)
    private Long quantity;
}
