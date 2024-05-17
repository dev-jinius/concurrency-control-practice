package com.concurrency.ecommerce.user.domain.model;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.OptimisticLockException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Slf4j
public class UserPointDto {

    private Long userId;
    private BigInteger point;
    private Long version;

    public UserPointDto(Long userId, BigInteger point) {
        this.userId = userId;
        this.point = point;
    }

    //포인트 충전(낙관적 락 구현 시 @Version 사용하면 자동으로 버전 관리)
    public void addPoint(BigInteger point) {
        this.point = this.point.add(point);
    }
}
