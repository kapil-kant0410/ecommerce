package com.ql.ecommerce.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean status;
    private int code;
    private String message;
    private T data;
    private T errors;

    public static <T> ApiResponse<T> success(int code ,T data ,String message){
        return ApiResponse.<T>builder().status(true).code(code).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> error(int code,T errors,String message){
        return ApiResponse.<T>builder().status(false).code(code).message(message).errors(errors).build();
    }

}

