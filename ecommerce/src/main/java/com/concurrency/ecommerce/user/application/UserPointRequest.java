package com.concurrency.ecommerce.user.application;

import lombok.*;

import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPointRequest {

    private Long userId;                //유저 ID
    private BigInteger point;           //포인트

    public UserPointRequest(Long userId) {
        this.userId = userId;
    }
}
