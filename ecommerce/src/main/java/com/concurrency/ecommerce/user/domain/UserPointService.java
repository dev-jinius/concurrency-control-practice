package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPointService {

    private final UserPointRepository userPointRepository;

    //포인트 조회 (낙관적 락)
    public UserPointDto getPoint(Long userId) {
        return userPointRepository.getUserPoint(userId).orElseThrow(() -> new EcommerceException(ErrorCode.NOT_FOUND_USER));
    }

    //포인트 조회 (분산 락)
    public UserPointDto point(Long userId) {
        return userPointRepository.getUserPointRedisson(userId).orElseThrow(() -> new EcommerceException(ErrorCode.NOT_FOUND_USER));
    }

    //포인트 충전 (User 엔티티에 @Version 사용한 낙관적 락 적용)
    @Transactional
    public UserPointDto chargePoint(UserPointDto userPointDto) {
        try {
            //유저 포인트 조회
            UserPointDto userPoint = getPoint(userPointDto.getUserId());
            log.info("before save point version [{}]",userPoint.getVersion());

            //포인트 충전
            userPoint.addPoint(userPointDto.getPoint());

            //포인트 저장
            UserPointDto result = userPointRepository.saveUserPoint(userPoint);
            log.info("after save point version [{}]",result.getVersion());

            return result;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new EcommerceException(ErrorCode.FAILED_OPTIMISTIC_LOCK);
        }
    }

    //포인트 충전 (분산 락 적용)
    @Transactional
    public UserPointDto chargePointRedisson(UserPointDto userPointDto) {
        //유저 포인트 조회
        UserPointDto userPoint = point(userPointDto.getUserId());
        //포인트 충전
        userPoint.addPoint(userPointDto.getPoint());
        //포인트 저장
        return userPointRepository.saveUserPoint(userPoint);
    }
}
