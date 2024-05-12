package com.concurrency.ecommerce.user.application;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.UserValidator;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimisticLockUserPointFacade {

    private final UserValidator userValidator;
    private final UserPointService userPointService;

    /**
     * 유저 포인트 조회
     * @return
     */
    public UserPointResponse point(Long userId) {
        userValidator.validateUser(userId);
        return UserPointResponse.of(userPointService.getPoint(userId));
    }

    /**
     * 유저 포인트 충전 with Optimistic Lock
     * @return
     */
    public UserPointResponse charge(UserPointRequest request) {
        log.info("[{}] 충전 요청", Thread.currentThread().getName());

        userValidator.validateUserPoint(request.toDomain());
        UserPointDto userPointDto = null;
        try {
            log.info("[before charge] version {}", userPointService.getPoint(request.getUserId()).getVersion());
            userPointDto = userPointService.chargePoint(request.toDomain());
            log.info("[{}] 충전 성공", Thread.currentThread().getName());
            log.info("[after charge] version {}", userPointDto.getVersion());
        } catch (OptimisticLockingFailureException e) {
            log.info("낙관적 락으로 인한 실패");
        }
        return UserPointResponse.of(userPointDto);
    }
}
