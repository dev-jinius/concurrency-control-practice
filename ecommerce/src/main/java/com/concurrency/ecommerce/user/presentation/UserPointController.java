package com.concurrency.ecommerce.user.presentation;

import com.concurrency.ecommerce.user.application.UserFacade;
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

    private final UserFacade userFacade;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 포인트 조회 API", description = "유저 포인트 조회")
    public ResponseEntity<UserPointResponse> point(@PathVariable(value = "userId") Long userId) {
        UserPointResponse response = userFacade.point(userId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping
    @Operation(summary = "유저 포인트 충전 API", description = "유저 포인트 충전")
    public ResponseEntity<UserPointResponse> point(UserPointRequest request) {
        UserPointResponse response = userFacade.charge(request);
        return ResponseEntity.ok().body(response);
    }
}
