package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;

import java.util.Optional;

public interface UserPointRepository {

    Optional<UserPointDto> getUserPoint(Long userId);

    UserPointDto saveUserPoint(UserPointDto dto);
}