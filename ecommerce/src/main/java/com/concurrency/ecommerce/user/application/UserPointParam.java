package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPointParam {

    private Long userId;                //유저 ID
    private BigInteger point;           //포인트

    public UserPointDto toDomain() {
        return UserPointDto.builder()
                .userId(userId)
                .point(point)
                .version(0L)
                .build();
    }

    public static UserPointParam of(UserPointDto dto) {
        return UserPointParam.builder()
                .userId(dto.getUserId())
                .point(dto.getPoint())
                .build();
    }
}
