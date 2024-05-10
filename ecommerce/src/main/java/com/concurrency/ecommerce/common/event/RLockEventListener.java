package com.concurrency.ecommerce.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RLockEventListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void subscribeUnlock(RLockEvent rLockEvent) {
        try {
            rLockEvent.unlock();
            log.info("unlock success. key={}", rLockEvent.getKey());
        } catch (IllegalMonitorStateException e) {
            log.info("already unlocked. key={}", rLockEvent.getLock());
        }
    }
}
