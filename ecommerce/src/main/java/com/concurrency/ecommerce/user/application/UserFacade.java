package com.concurrency.ecommerce.user.application;

import org.springframework.stereotype.Service;

@Service
public class UserFacade {

    /**
     * 유저 포인트 조회
     * @return
     */
    public UserPointResponse point(Long userId) {
        return null;
    }

    /**
     * 유저 포인트 충전
     * @return
     */
    public UserPointResponse charge(UserPointRequest request) {
        return null;
    }
}
