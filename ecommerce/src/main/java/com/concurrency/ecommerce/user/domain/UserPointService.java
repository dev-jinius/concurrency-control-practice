package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.common.aop.DistributedLock;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPointService {

    private final UserPointRepository userPointRepository;

    //포인트 조회
    public UserPointDto getPoint(Long userId) {
        return userPointRepository.getUserPointWithOptimisticLock(userId).orElseThrow(() -> new EcommerceException(ErrorCode.NOT_FOUND_USER));
    }

    //포인트 업데이트 (Optimistic Lock)
    public UserPointDto savePoint(UserPointDto userPointDto) {
        UserPointDto result = null;
        try {
            result = userPointRepository.saveUserPoint(userPointDto);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.info("낙관적 락으로 인한 포인트 업데이트 실패");
            throw new EcommerceException(ErrorCode.FAILED_OPTIMISTIC_LOCK);
        }
        return result;
    }

    //포인트 업데이트 (Distributed Lock)
    public UserPointDto savePointWithDistributedLock(UserPointDto userPointDto) {
        return userPointRepository.saveUserPoint(userPointDto);
    }
}
