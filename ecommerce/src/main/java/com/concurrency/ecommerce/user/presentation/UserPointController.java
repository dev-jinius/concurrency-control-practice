package com.concurrency.ecommerce.user.presentation;

import com.concurrency.ecommerce.user.application.RedissonLockUserPointFacade;
import com.concurrency.ecommerce.user.application.OptimisticLockUserPointFacade;
import com.concurrency.ecommerce.user.application.UserPointRequest;
import com.concurrency.ecommerce.user.application.UserPointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecommerce/user/point")
@RequiredArgsConstructor
@Tag(name = "UserPoint", description = "유저 포인트 API")
public class UserPointController {

    private final OptimisticLockUserPointFacade optimisticLockUserPointFacade;
    private final RedissonLockUserPointFacade redissonLockUserPointFacade;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 포인트 조회 API", description = "유저 포인트 조회")
    public ResponseEntity<UserPointResponse> point(@PathVariable(value = "userId") Long userId) {
        UserPointResponse response = optimisticLockUserPointFacade.point(userId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping
    @Operation(summary = "유저 포인트 충전 API", description = "유저 포인트 충전")
    public ResponseEntity<UserPointResponse> point(@RequestBody UserPointRequest request) throws InterruptedException {
        UserPointResponse response = optimisticLockUserPointFacade.charge(request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/lock")
    @Operation(summary = "유저 포인트 충전 API", description = "유저 포인트 충전 RLock 추가")
    public ResponseEntity<UserPointResponse> pointWithRLock(@RequestBody UserPointRequest request) {
        UserPointResponse response = redissonLockUserPointFacade.executeCharge(request);
        return ResponseEntity.ok().body(response);
    }
}
