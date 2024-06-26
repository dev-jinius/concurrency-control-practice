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
public class UserPointRequest {

    @NotNull(message = "id는 필수값입니다.")
    private Long userId;                //유저 ID

    @NotNull(message = "포인트는 필수값입니다.")
    @Min(value = 1, message = "0 포인트 이상 가능합니다.")
    private BigInteger point;           //포인트

    public UserPointRequest(Long userId) {
        this.userId = userId;
    }

    public UserPointDto toDomain() {
        return UserPointDto.builder()
                .userId(userId)
                .point(point)
                .version(0L)
                .build();
    }
}
