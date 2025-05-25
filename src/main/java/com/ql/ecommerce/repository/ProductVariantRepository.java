package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> , JpaSpecificationExecutor<ProductVariant> {
    List<ProductVariant> findByProductIdAndIdNot(Long productId,Long productVariantId);
}
