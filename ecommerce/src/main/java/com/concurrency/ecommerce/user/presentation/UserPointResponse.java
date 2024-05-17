package com.concurrency.ecommerce.user.presentation;

import com.concurrency.ecommerce.user.application.UserPointParam;
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

    public static UserPointResponse of(UserPointParam param) {
        return UserPointResponse.builder()
                .userId(param.getUserId())
                .point(param.getPoint())
                .build();
    }
}
