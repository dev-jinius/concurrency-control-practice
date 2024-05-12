package com.concurrency.ecommerce.user.domain;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import com.concurrency.ecommerce.user.infra.User;
import com.concurrency.ecommerce.user.infra.UserPointJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointJpaRepository userPointJpaRepository;

    @Override
    public Optional<UserPointDto> getUserPoint(Long userId) {
        return userPointJpaRepository.findById(userId).map(user -> user.toDomain());
    }

    @Override
    public UserPointDto saveUserPoint(UserPointDto dto) {
        return userPointJpaRepository.saveAndFlush(User.fromDomain(dto)).toDomain();
    }
}
