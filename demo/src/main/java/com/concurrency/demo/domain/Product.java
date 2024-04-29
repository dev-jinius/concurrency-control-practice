package com.concurrency.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;     // 상품 ID
    private String name;        // 상품명
    private Long price;         // 상품 가격
    private Long stock;         // 재고 수량

    public Product(Long productId, Long stock) {
        this.productId = productId;
        this.stock = stock;
    }

    public void decreaseStock(Long quantity) throws Exception {
        if (this.stock < quantity || this.stock == 0) {
            throw new Exception("재고 부족");
        } else {
            this.stock -= quantity;
        }
    }
}
