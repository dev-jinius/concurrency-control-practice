package com.concurrency.ecommerce.user.presentation;

import com.concurrency.ecommerce.user.application.UserPointFacade;
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

    private final UserPointFacade userPointFacade;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 포인트 조회 API", description = "유저 포인트 조회")
    public ResponseEntity<UserPointResponse> point(@PathVariable(value = "userId") Long userId) {
        UserPointResponse response = UserPointResponse.of(userPointFacade.point(userId));
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/optimistic")
    @Operation(summary = "유저 포인트 충전 API", description = "유저 포인트 충전")
    public ResponseEntity<UserPointResponse> charge(@RequestBody UserPointRequest request) {
        UserPointResponse response = UserPointResponse.of(userPointFacade.charge(request.toParam()));
        return ResponseEntity.ok().body(response);
    }
}
