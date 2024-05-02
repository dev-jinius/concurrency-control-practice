package com.concurrency.demo.domain;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RequestProduct {
    private Long productId;
    private Long quantity;
}
