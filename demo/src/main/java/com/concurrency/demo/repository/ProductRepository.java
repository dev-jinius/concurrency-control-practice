package com.concurrency.demo.repository;

import com.concurrency.demo.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //상품 조회
    //Order By : 비관적 락 사용 시 데드락 방지.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    List<Product> findByProductIdInOrderByProductId(List<Long> ids);

}