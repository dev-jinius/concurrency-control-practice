package com.concurrency.ecommerce.user.application;

import lombok.*;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserPointResponse {
    private Long userId;                //유저 ID
    private BigInteger point;           //포인트

//    public static UserPointResponse of(UserPointDto dto) {
//        return UserPointResponse.builder()
//                .userId(dto.getId())
//                .point(dto.getPoint())
//                .build();
//    }
}
