package com.concurrency.ecommerce.common;

import com.concurrency.ecommerce.common.event.RLockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockHandler {

    private static final String REDISSON_LOCK_PREFIX = "RLOCK_";
    private final RedissonClient redissonClient;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisLockTransactionHandler redisLockTransactionHandler;

    public <T> T callWithLock(String lockKey, Supplier<T> supplier, long waitTime, long leaseTime, TimeUnit unit) {
        String key = REDISSON_LOCK_PREFIX + lockKey;
        final RLock lock = redissonClient.getLock(key);
        return this.execute(key, lock, supplier, waitTime, leaseTime, unit);
    }

    private <T> T execute(String key, RLock lock, Supplier<T> supplier, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            //락 획득 시도
            //waitTime: 락 대기 타임아웃. 시간 내 락을 획득하지 못하면 false 반환, 성공 시 true 반환.
            //leaseTime: 락 만료 타임아웃. Application이 어떤 문제로 락 해제를 못하더라도 자동으로 락을 해제해준다.
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                //락 획득 성공
                log.info("get lock success. key={}", key);

                //transaction을 락 획득 이후에 시작한다.
                //@Transactional은 AOP(proxy 기반)로 작동하기 때문에 같은 클래스 내부에서 호출하면 작동되지 않는다.
                //실행 관련 콜백 메서드는 redisLockTransactionHandler에게 위임해서 실행한다.
                return redisLockTransactionHandler.execute(supplier);
            }
            // 락 획득 실패
            throw new InterruptedException();
        } catch (InterruptedException e) {
            log.info("get lock failed. key={}", key);
            throw new EcommerceException(ErrorCode.FAILED_ACQUIRE_RLOCK);
        } finally {
            //RLock 해제 이벤트 발행 => 트랜잭션 완료(커밋, 롤백) 후에 락 해제하도록 AFTER_COMPLETION 설정.
            //트랜잭션 완료 이후 락 해제 => 데이터의 정합성을 위함.
            applicationEventPublisher.publishEvent(new RLockEvent(key, lock));
        }
    }
}
