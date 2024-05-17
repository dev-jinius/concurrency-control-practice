package com.concurrency.ecommerce.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    public OrderResponse order(OrderRequest request) {
        //결제 요청 검증
        //유저 포인트 차감
        //결제 정보 저장
        //주문 정보 전달
        return null;
    }
}
