package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.common.RedisLockHandler;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MariaDB isloation level - REPEATABLE READ로 `UPDATE`에 대한 동시성 제어
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPointFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;

    private static final String LOCK_KEY_PREFIX = "UserPoint_";
    private final RedisLockHandler redisLockHandler;

    /**
     * 유저 포인트 조회
     *
     * @return
     */
    @Transactional
    public UserPointParam point(Long userId) {
        userValidator.validateUser(userId);
        return UserPointParam.of(userPointService.point(userId));
    }

    /**
     * 포인트 충전 (낙관적 락 적용)
     * @param param
     * @return
     */
    @Transactional
    public UserPointParam charge(UserPointParam param) {
        log.info("[{}] 충전 요청", Thread.currentThread().getName());

        userValidator.validateUserPoint(param.toDomain());
        UserPointDto afterUser = userPointService.chargePoint(param.toDomain());

        log.info("[{}] 충전 완료", Thread.currentThread().getName());
        return UserPointParam.of(afterUser);
    }

    /**
     * 포인트 충전 (분산락 적용)
     * @param param
     * @return
     */
    @Transactional
    public UserPointParam executeCharge(UserPointParam param) {
        userValidator.validateUserPoint(param.toDomain());
        String key = LOCK_KEY_PREFIX + param.getUserId();

        //Lock 획득 시 waitTiem, leaseTime 커스텀
        //return redisLockHandler.tryLock(key, () -> UserPointParam.of(userPointService.chargePointRedisson(param.toDomain())), 5, 3, TimeUnit.SECONDS);

        //바로 Lock을 획득하도록 함.
        return redisLockHandler.lock(key, () -> UserPointParam.of(userPointService.chargePointRedisson(param.toDomain())));
    }
}
