package com.concurrency.ecommerce.common.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RLockEvent {

    private String key;
    private RLock lock;

    public void unlock() {
        log.info("unlock success. key={}", key);
        this.lock.unlock();
    }
}
