package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.common.aop.DistributedLock;
import com.concurrency.ecommerce.common.config.RedissonConfig;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPointFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;

    private final RedissonClient redissonClient;

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
     * 유저 포인트 충전 with Optimistic Lock
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
    @Transactional
    public UserPointResponse chargeWithRedisson(UserPointRequest request) {
        String REDISSON_LOCK_PREFIX = "UserPoint_";
        String key = REDISSON_LOCK_PREFIX + request.getUserId();
        RLock lock = redissonClient.getLock(key);
        try {
            //락 획득 시도
            //waitTime: 락 대기 타임아웃. 시간 내 락을 획득하지 못하면 false 반환, 성공 시 true 반환.
            //leaseTime: 락 만료 타임아웃. Application이 어떤 문제로 락 해제를 못하더라도 자동으로 락을 해제해준다.
            if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                log.info("get lock success. key={}", key);

                //request 유효성 검사
                userValidator.validateUserPoint(request.toDomain());
                //유저 포인트 조회
                UserPointDto userPoint = userPointService.getPoint(request.getUserId());
                //포인트 충전
                userPoint.addPoint(request.getPoint());
                //포인트 업데이트
                return UserPointResponse.of(userPointService.savePoint(userPoint));
            }
            //락 획득 실패
            throw new InterruptedException();
        } catch (InterruptedException e) {
            throw new EcommerceException(ErrorCode.FAILED_ACQUIRE_RLOCK);
        } finally {
              //락 해제
            lock.unlock();
            log.info("unlock success. key={}", key);
        }
    }
}
