package com.concurrency.ecommerce.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * 트랜잭션 분리용
 */
@Component
@Slf4j
public class RedisLockTransactionHandler {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T execute(final Supplier<T> supplier) {
        log.info("execute");
        return supplier.get();
    }
}
