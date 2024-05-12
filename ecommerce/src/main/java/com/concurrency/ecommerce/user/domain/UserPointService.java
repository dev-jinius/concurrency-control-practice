package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPointService {

    private final UserPointRepository userPointRepository;

    //포인트 조회 (낙관적 락 적용)
    public UserPointDto getPoint(Long userId) {
        return userPointRepository.getUserPoint(userId).orElseThrow(() -> new EcommerceException(ErrorCode.NOT_FOUND_USER));
    }

    //포인트 충전
    @Transactional
    public UserPointDto chargePoint(UserPointDto userPointDto) {
        //유저 포인트 조회
        UserPointDto userPoint = getPoint(userPointDto.getUserId());
        //포인트 충전
        userPoint.addPoint(userPointDto.getPoint());
        //포인트 저장
        return userPointRepository.saveUserPoint(userPoint);
    }
}
