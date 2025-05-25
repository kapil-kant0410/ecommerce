package com.ql.ecommerce.repository;

import com.ql.ecommerce.entity.Category;
import com.ql.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {
    List<Product> findTop10ByCategoryIdAndIdNot(Long categoryId,Long productId);
    List<Product> findTop10ByBrandAndIdNot(String brand,Long productId);
}
