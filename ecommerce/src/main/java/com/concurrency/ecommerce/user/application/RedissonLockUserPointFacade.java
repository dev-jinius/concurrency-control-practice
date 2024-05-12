package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.common.RedisLockHandler;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockUserPointFacade {
    private static final String LOCK_KEY_PREFIX = "UserPoint_";

    private final UserValidator userValidator;
    private final UserPointService userPointService;
    private final RedisLockHandler redisLockHandler;

    /**
     * 포인트 충전
     * @param request UserPointRequest
     * @return UserPointResponse
     */
    public UserPointResponse executeCharge(UserPointRequest request) {
        userValidator.validateUserPoint(request.toDomain());
        String key = LOCK_KEY_PREFIX + request.getUserId();
        return redisLockHandler.callWithLock(key, () -> UserPointResponse.of(userPointService.chargePoint(request.toDomain())), 5, 3, TimeUnit.SECONDS);
    }
}
