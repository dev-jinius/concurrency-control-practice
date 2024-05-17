package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigInteger;

@Component
public class UserValidator {

    public void validateUser(Long userId) {
        if (ObjectUtils.isEmpty(userId) || userId <= 0) throw new EcommerceException(ErrorCode.INVALID_PARAMETER);
    }

    public void validatePoint(BigInteger point) {
        if (ObjectUtils.isEmpty(point) || point.compareTo(BigInteger.ZERO) <= 0) throw new EcommerceException(ErrorCode.INVALID_PARAMETER);
    }

    public void validateUserPoint(UserPointDto userPointDto) {
        if (ObjectUtils.isEmpty(userPointDto)) throw new EcommerceException(ErrorCode.INVALID_PARAMETER);
        validateUser(userPointDto.getUserId());
        validatePoint(userPointDto.getPoint());
    }
}
