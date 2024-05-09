package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserValidator {

    public void validateUser(Long userId) {
        if (ObjectUtils.isEmpty(userId)) throw new EcommerceException(ErrorCode.NOT_FOUND_USER);
    }

    public void validateUserPoint(UserPointDto userPointDto) {
        if (ObjectUtils.isEmpty(userPointDto)) throw new EcommerceException(ErrorCode.NOT_FOUND_USER);
    }
}
