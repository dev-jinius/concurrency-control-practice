package com.concurrency.ecommerce.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointJpaRepository extends JpaRepository<User, Long> {
}
