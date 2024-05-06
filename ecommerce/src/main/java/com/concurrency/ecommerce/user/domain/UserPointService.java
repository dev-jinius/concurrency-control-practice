package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;

    //포인트 조회
    public UserPointDto getPoint(Long userId) {
        return userPointRepository.getUserPoint(userId).orElseThrow(() -> new EcommerceException(ErrorCode.NOT_FOUND_USER));
    }

    //포인트 충전
    public void charge() {

    }
}
