package com.ql.ecommerce.util;

import com.ql.ecommerce.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseBuilder {

    public <T> ResponseEntity<ApiResponse<Map<String, Object>>> build(String key, T value, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put(key, value);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                message
        ));
    }

    public <T> ResponseEntity<ApiResponse<Map<String, Object>>> build(Map<String, Object> data, String message) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                data,
                message
        ));
    }

    public ResponseEntity<ApiResponse<Map<String, Object>>> error(HttpStatus status, Map<String, Object> data, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(
                status.value(), data, message
        ));
    }

}
