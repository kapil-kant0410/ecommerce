package com.ql.ecommerce.specification;

import com.ql.ecommerce.entity.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ProductSpecification {

    public Specification<Product> hasCategory(String categorySlugOrId) {
        return (root, query, cb) -> {
            if (categorySlugOrId == null) return null;

            Join<Object, Object> categoryJoin = root.join("category");

            try {
                Long id = Long.parseLong(categorySlugOrId);
                return cb.equal(categoryJoin.get("id"), id);
            } catch (NumberFormatException e) {
                return cb.equal(categoryJoin.get("slug"), categorySlugOrId);
            }
        };
    }

    public Specification<Product> hasBrandIn(List<String> brands) {
        return (root, query, cb) ->
                (brands == null || brands.isEmpty()) ? null : root.get("brand").in(brands);
    }
}

















