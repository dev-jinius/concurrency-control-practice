package com.concurrency.ecommerce.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EcommerceException extends RuntimeException {
    private ErrorCode errorCode;
}
