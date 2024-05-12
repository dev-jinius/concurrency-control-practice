package com.concurrency.ecommerce.order.presentation;

import com.concurrency.ecommerce.order.application.OrderRequest;
import com.concurrency.ecommerce.order.application.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecommerce/order")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 및 결제 API")
public class OrderController {

    private final OrderFacade orderFacade;
    @PostMapping
    @Operation(summary = "주문 및 결제 API", description = "주문서 생성 및 주문 결제하는 API")
    public ResponseEntity<OrderResponse> order(OrderRequest orderRequest) {
        OrderResponse result = orderFacade.order(orderRequest);

        return ResponseEntity.ok().body(null);
    }

}
