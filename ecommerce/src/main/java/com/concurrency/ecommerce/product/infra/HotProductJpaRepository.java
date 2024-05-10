package com.concurrency.ecommerce.product.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotProductJpaRepository extends JpaRepository<HotProduct, Long> {
    
}
