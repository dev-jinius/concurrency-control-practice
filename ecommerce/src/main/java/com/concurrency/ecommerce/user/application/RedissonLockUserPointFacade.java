package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.common.RedisLockHandler;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockUserPointFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;
    private final RedisLockHandler redisLockHandler;
    private static final String LOCK_KEY_PREFIX = "UserPoint_";

    public UserPointResponse execute(UserPointRequest request) {
        String key = LOCK_KEY_PREFIX + request.getUserId();
        return redisLockHandler.callWithLock(key, () -> charge(request), 5, 3, TimeUnit.SECONDS);
    }

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
