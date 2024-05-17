package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MariaDB isloation level - REPEATABLE READ로 `UPDATE`에 대한 동시성 제어
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPointFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;

    /**
     * 유저 포인트 조회
     *
     * @return
     */
    public UserPointParam point(Long userId) {
        userValidator.validateUser(userId);
        return UserPointParam.of(userPointService.getPoint(userId));
    }

    /**
     * 포인트 충전 (낙관적 락 적용)
     * @param param
     * @return
     */
    public UserPointParam charge(UserPointParam param) {
        log.info("[{}] 충전 요청", Thread.currentThread().getName());

        userValidator.validateUserPoint(param.toDomain());
        UserPointDto afterUser = userPointService.chargePoint(param.toDomain());

        log.info("[{}] 충전 완료", Thread.currentThread().getName());
        return UserPointParam.of(afterUser);
    }
}
