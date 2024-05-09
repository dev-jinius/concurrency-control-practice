package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.*;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserPointResponse {
    private Long userId;                //유저 ID
    private BigInteger point;           //포인트

    public static UserPointResponse of(UserPointDto dto) {
        return UserPointResponse.builder()
                .userId(dto.getUserId())
                .point(dto.getPoint())
                .build();
    }
}
