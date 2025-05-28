package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ProductVariant.ProductVariantDto;
import com.ql.ecommerce.dto.product.response.ProductDto;
import com.ql.ecommerce.entity.Product;
import com.ql.ecommerce.service.RecentlyViewedService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedServiceImpl implements RecentlyViewedService {

    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${max.recently.viewed}")
    private Long maxRecentlyViewed;

    public RecentlyViewedServiceImpl(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    private String getProductKey(Long userId) {
        return "recently_viewed:product:" + userId;
    }

    private String getVariantKey(Long userId) {
        return "recently_viewed:variant:" + userId;
    }

    public void addToRecentlyViewedProducts(Long userId, ProductDto productdto) {
         String key = getProductKey(userId);

        // Remove product if it already exists (avoid duplicates)
        redisTemplate.opsForList().remove(key, 1, productdto);

        // Push to beginning (most recent)
        redisTemplate.opsForList().leftPush(key, productdto);

        // Trim to max size
        redisTemplate.opsForList().trim(key, 0, maxRecentlyViewed - 1);

        // Optional: Expire in 7 days
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public List<ProductDto> getRecentlyViewedProducts(Long userId){
        String key = getProductKey(userId);

        List<Object> redisObjects = redisTemplate.opsForList().range(key, 0, -1);
        if (redisObjects == null) return Collections.emptyList();

        return redisObjects.stream()
                .filter(ProductDto.class::isInstance)
                .map(obj -> (ProductDto) obj)
                .collect(Collectors.toList());
    }

    // ====  Variant Methods ====

    public void addToRecentlyViewedVariants(Long userId, ProductVariantDto productVariantDto) {
        String key = getVariantKey(userId);

        redisTemplate.opsForList().remove(key, 1, productVariantDto);
        redisTemplate.opsForList().leftPush(key, productVariantDto);
        redisTemplate.opsForList().trim(key, 0, maxRecentlyViewed - 1);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public List<ProductVariantDto> getRecentlyViewedVariants(Long userId){
        List<Object> redisObjects = redisTemplate.opsForList().range(getVariantKey(userId), 0, -1);
        if (redisObjects == null) return Collections.emptyList();

        return redisObjects.stream()
                .filter(ProductVariantDto.class::isInstance)
                .map(obj -> (ProductVariantDto) obj)
                .collect(Collectors.toList());
    }





}
