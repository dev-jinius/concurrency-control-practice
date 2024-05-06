package com.concurrency.ecommerce.user.domain.model;

import lombok.*;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserPointDto {

    private Long userId;
    private BigInteger point;
}
