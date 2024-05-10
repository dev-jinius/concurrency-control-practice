package com.concurrency.ecommerce.product.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class HotProduct {

    @Id
    private Long id;
    private String name;
    private Long totalQuantityLast3Days;
}
