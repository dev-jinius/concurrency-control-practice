package com.concurrency.ecommerce.user.infra;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointJpaRepository extends JpaRepository<User, Long> {

    // 엔티티 조회 -> 수정 시 낙관적 락 적용 => 조회한 엔티티는 트랜잭션을 종료할 때까지 다른 트랜잭션에서 변경하지 않는다.
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findById(@Param("userId") Long userId);
}
