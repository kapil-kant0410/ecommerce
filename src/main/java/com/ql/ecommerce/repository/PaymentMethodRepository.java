package com.ql.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ql.ecommerce.entity.PaymentMethod;
import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
     List<PaymentMethod> findByUserId(Long userId);
}
