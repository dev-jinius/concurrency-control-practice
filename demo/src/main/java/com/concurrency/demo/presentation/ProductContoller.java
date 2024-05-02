package com.concurrency.demo.presentation;

import com.concurrency.demo.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/product")
@RequiredArgsConstructor
public class ProductContoller {

    private final ProductService productService;
    private final SynchronizedProductService synchronizedProductService;
    private final ConcurrentHashMapProductService concurrentHashMapProductService;
    private final ReentrantLockProductService reentrantLockProductService;

    @PatchMapping("/decrease")
    public ResponseEntity<Response> decreaseNoConCurrency(@RequestBody Request request) throws Exception {
        Product result = productService.decreaseStock(request.toDomain());

        return ResponseEntity.ok().body(Response.of(result));
    }

    @PatchMapping("/synchronized/decrease")
    public ResponseEntity<Response> decreaseBySynchronized(@RequestBody Request request) throws Exception {
        Product result = synchronizedProductService.decreaseStock(request.toDomain());

        return ResponseEntity.ok().body(Response.of(result));
    }

    @PatchMapping("/concurrent/decrease")
    public ResponseEntity<Response> decreaseByConcurrent(@RequestBody Request request) throws Exception {
        Product result = concurrentHashMapProductService.decreaseStock(request.toDomain());

        return ResponseEntity.ok().body(Response.of(result));
    }

    @PatchMapping("/reentrantlock/decrease")
    public ResponseEntity<Response> decreaseByReentrantLock(@RequestBody Request request) throws Exception {
        Product result = reentrantLockProductService.decreaseStock(request.toDomain());

        return ResponseEntity.ok().body(Response.of(result));
    }
}
