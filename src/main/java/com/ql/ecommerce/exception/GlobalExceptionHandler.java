package com.ql.ecommerce.exception;

import com.ql.ecommerce.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //Global fallback exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleGlobalException(Exception ex, HttpServletRequest request){
         logger.error("Unhandled exception occurred at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
         Map<String, String> errorDetails = new HashMap<>();
         errorDetails.put("error", "Internal Server Error");
         errorDetails.put("path", request.getRequestURI());
         errorDetails.put("timestamp", Instant.now().toString());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails,ex.getMessage()));
    }

    //No endpoint for the given requested endpoint
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails, "No static resource " + ex.getResourcePath()));
    }

    //Handler for all resource-not-found-type exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleResourceNotFoundException(ResourceNotFoundException ex,HttpServletRequest request){
        logger.warn("Resource not found at [{}]: {}", request.getRequestURI(), ex.getMessage());
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", "NOT FOUND");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), errorDetails,ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleInvalidTokenException(InvalidTokenException ex,HttpServletRequest request){
        logger.warn("Invalid token at [{}]: {}", request.getRequestURI(), ex.getMessage());
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorDetails,ex.getMessage()));
    }

    //Invalid email and password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBadCredentials(BadCredentialsException ex,HttpServletRequest request) {
        logger.warn("Invalid credentials at [{}]: {}", request.getRequestURI(), ex.getMessage());
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "Invalid username or password");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), errorDetails,ex.getMessage()));
    }

    //Handles Token expiration in ExpiredJwtException
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleTokenExpiredException(TokenExpiredException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        errorDetails,
                        ex.getMessage()
                ));
    }

    //Handle exception when missing token
    @ExceptionHandler(MissingTokenException.class) // Handles only MissingTokenException
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingTokenException(MissingTokenException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        errorDetails,
                        ex.getMessage()
                ));
    }

    //handel exception for blacklisted tokens
    @ExceptionHandler(BlacklistedTokenException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBlacklistedTokenException(BlacklistedTokenException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase()); // "Unauthorized"
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        errorDetails,
                        ex.getMessage()
                ));
    }

    //handel user if it is unauthenticated
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        Map<String, String> data = new HashMap<>();
        data.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        data.put("path", request.getRequestURI());
        data.put("timestamp", Instant.now().toString());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        HttpStatus.FORBIDDEN.value(),
                        data,
                        ex.getMessage()
                ));
    }

    //When a required query parameter or form parameter is missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingRequestParamException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("parameter", ex.getParameterName()); // which parameter was missing
        errorDetails.put("path", request.getRequestURI());    // API path
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        errorDetails,
                        "Missing required request parameter: " + ex.getParameterName()
                ));
    }

    //when a required path variable is missing in a controller method
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());                            // The endpoint hit
        errorDetails.put("timestamp", Instant.now().toString());                      // Current time
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());         // "Bad Request"

        // Add the name of the missing variable
        errorDetails.put("missingPathVariable", ex.getVariableName());

        // Return structured error response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        errorDetails,
                        "Missing path variable: " + ex.getVariableName()
                ));
    }

    //This exception is thrown when validation on a request body annotated with @Valid fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrorsException(MethodArgumentNotValidException ex, HttpServletRequest request) {
       Map<String, String> errorDetails = new HashMap<>();
       errorDetails.put("path", request.getRequestURI());
       errorDetails.put("timestamp", Instant.now().toString());
       errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

       // Add field-specific validation errors
       ex.getBindingResult().getFieldErrors().forEach(fieldError -> errorDetails.put(fieldError.getField(), fieldError.getDefaultMessage()));

       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(
                       HttpStatus.BAD_REQUEST.value(),
                       errorDetails,
                       "Validation failed for request body"
               ));
   }

    //When the request body is missing or contains malformed/invalid JSON that cannot be deserialized
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
       Map<String, String> errorDetails = new HashMap<>();
       errorDetails.put("path", request.getRequestURI());
       errorDetails.put("timestamp", Instant.now().toString());
       errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

       // Return structured ApiResponse
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(
                       HttpStatus.BAD_REQUEST.value(),
                       errorDetails,
                       "Invalid or malformed JSON request body"
               ));
   }

    //Request parameter or path variable can't be converted to the required data type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());                                      // URI that failed
        errorDetails.put("timestamp", Instant.now().toString());                                // When it failed
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());                   // "Bad Request"
        errorDetails.put("parameterName", ex.getName());                                        // e.g. "id"
        errorDetails.put("invalidValue", String.valueOf(ex.getValue()));                        // e.g. "abc"
        errorDetails.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        errorDetails,
                        "Invalid type for parameter: " + ex.getName()
                ));
    }

    //Request using an HTTP method (GET, POST, PUT, DELETE, etc.) that is not supported by the endpoint
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        errorDetails.put("unsupportedMethod", ex.getMethod());
        errorDetails.put("supportedMethods", String.join(", ", ex.getSupportedMethods()));

        // Return response in consistent format
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        errorDetails,
                        "HTTP method not supported for this endpoint"
                ));
    }

    //Content-Type (e.g., application/json, multipart/form-data, etc.) of the request is not supported by the API endpoint
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());        // Unsupported Media Type
        errorDetails.put("unsupportedMediaType", ex.getContentType() != null ? ex.getContentType().toString() : "Unknown");
        errorDetails.put("supportedMediaTypes", ex.getSupportedMediaTypes().toString());       // Allowed types
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        errorDetails,
                        "Content-Type not supported"
                ));
    }

    //If email is null in authentication context
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("timestamp", Instant.now().toString());
        errorDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        errorDetails,
                        ex.getMessage()
                ));
    }

}
