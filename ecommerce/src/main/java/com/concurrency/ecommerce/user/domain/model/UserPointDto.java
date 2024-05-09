package com.concurrency.ecommerce.user.domain.model;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserPointDto {

    private Long userId;
    private BigInteger point;
    private Long version;

    public UserPointDto(Long userId, BigInteger point) {
        this.userId = userId;
        this.point = point;
    }

    //포인트 충전
    public void addPoint(BigInteger point) {
        this.point = this.point.add(point);
    }
}
