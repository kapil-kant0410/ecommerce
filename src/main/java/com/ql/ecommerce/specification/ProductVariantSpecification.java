package com.ql.ecommerce.specification;

import com.ql.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductVariantSpecification {

    public Specification<ProductVariant> hasColorIn(List<String> colors) {
        return (root, query, cb) -> {
            if (colors == null || colors.isEmpty()) return null;
            return root.get("color").in(colors);
        };
    }

    public Specification<ProductVariant> hasSizeIn(List<String> sizes) {
        return (root, query, cb) -> {
        if (sizes == null || sizes.isEmpty()) return null;
        return root.get("size").in(sizes);
        };
    }

    public Specification<ProductVariant> hasPriceBetween(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            } else {
                return null;
            }
        };
    }

    public  Specification<ProductVariant> hasMinimumRating(Integer minRating) {
        return (root, query, cb) -> {
            if (minRating != null) {
                return cb.greaterThanOrEqualTo(root.get("rating"), minRating);
            }
            return null; // No rating filter
        };
    }



}


