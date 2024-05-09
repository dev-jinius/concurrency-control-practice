package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import com.concurrency.ecommerce.user.infra.User;
import com.concurrency.ecommerce.user.infra.UserPointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointJpaRepository userPointJpaRepository;

    @Override
    public Optional<UserPointDto> getUserPointWithOptimisticLock(Long userId) {
        return userPointJpaRepository.findById(userId).map(user -> user.toDomain());
    }

    @Override
    public UserPointDto saveUserPoint(UserPointDto dto) {
        return userPointJpaRepository.saveAndFlush(User.fromDomain(dto)).toDomain();
    }
}
