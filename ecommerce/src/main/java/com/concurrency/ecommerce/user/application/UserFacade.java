package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;

    /**
     * 유저 포인트 조회
     * @return
     */
    @Transactional
    public UserPointResponse point(Long userId) {
        //request 유효성 검사
        userValidator.validateUser(userId);
        //포인트 조회
        return UserPointResponse.of(userPointService.getPoint(userId));
    }

    /**
     * 유저 포인트 충전
     * @return
     */
    @Transactional
    public UserPointResponse charge(UserPointRequest request) {
        //request 유효성 검사
        userValidator.validateUserPoint(request.toDomain());
        //유저 포인트 조회
        UserPointDto userPoint = userPointService.getPoint(request.getUserId());
        //포인트 충전
        userPoint.addPoint(request.getPoint());
        //포인트 업데이트
        return UserPointResponse.of(userPointService.savePoint(userPoint));
    }
}
