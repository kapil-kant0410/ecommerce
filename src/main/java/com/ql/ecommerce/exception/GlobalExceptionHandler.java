package com.ql.ecommerce.exception;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
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
    private final ResponseBuilder responseBuilder;

    public GlobalExceptionHandler(ResponseBuilder responseBuilder){
        this.responseBuilder=responseBuilder;
    }

    //Global fallback exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleGlobalException(Exception ex, HttpServletRequest request){
         logger.error("[GlobalExceptionHandler] Exception ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
         return responseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR,null,ex.getMessage());
    }

    //When a method argument is illegal (e.g., null where not allowed)
    //throw new IllegalArgumentException("ID must not be null")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("[GlobalExceptionHandler] IllegalArgumentException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, null, ex.getMessage());
    }

    //No endpoint for the given requested endpoint
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNoResourceFoundException(NoResourceFoundException ex) {
        logger.error("[GlobalExceptionHandler] NoResourceFoundException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                "No static resource found: " + ex.getResourcePath()
        );

    }

    //Catch general runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleRunTimeException(RuntimeException ex) {
        logger.error("[GlobalExceptionHandler] RuntimeException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, null, ex.getMessage());
    }

    //Remove this when u will add role based end points
    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleForbiddenException(Forbidden ex) {
        logger.error("[GlobalExceptionHandler] Forbidden ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.FORBIDDEN, null, ex.getMessage());
    }

    //Handler for all resource-not-found-type exceptions
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleResourceNotFoundException(ResourceNotFound ex,HttpServletRequest request){
        logger.error("[GlobalExceptionHandler] ResourceNotFound ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.NOT_FOUND, null, ex.getMessage());
    }

    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleInvalidTokenException(InvalidToken ex, HttpServletRequest request) {
        logger.error("[GlobalExceptionHandler] InvalidToken ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    //Invalid email and password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBadCredentials(BadCredentialsException ex) {
        logger.error("[GlobalExceptionHandler] BadCredentialsException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    //Handles Token expiration in ExpiredJwtException
    @ExceptionHandler(TokenExpired.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleTokenExpiredException(TokenExpired ex) {
        logger.error("[GlobalExceptionHandler] TokenExpired ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    //Handle exception when missing token in request
    @ExceptionHandler(MissingToken.class) // Handles only MissingTokenException
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMissingTokenException(MissingToken ex) {
        logger.error("[GlobalExceptionHandler] MissingToken ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    //handel exception for blacklisted tokens
    @ExceptionHandler(BlacklistedToken.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBlacklistedTokenException(BlacklistedToken ex) {
        logger.error("[GlobalExceptionHandler] BlacklistedToken ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    //Authenticated user is not authorized.
    @ExceptionHandler(AccessDenied.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAccessDeniedException(AccessDenied ex) {
        logger.error("[GlobalExceptionHandler] AccessDenied ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.FORBIDDEN, null, ex.getMessage());
    }

    //When a required query parameter or form parameter is missing
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
        logger.error("[GlobalExceptionHandler] MissingServletRequestParameterException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(
                HttpStatus.BAD_REQUEST,
                null,
                "Missing required request parameter: " + ex.getParameterName()
        );
    }

    //when a required path variable @Pathvariable is missing in a controller method
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMissingPathVariable(MissingPathVariableException ex) {
        logger.error("[GlobalExceptionHandler] MissingPathVariableException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(
                HttpStatus.BAD_REQUEST,
                null,
                "Missing path variable: " + ex.getVariableName()
        );
    }

    //This exception is thrown when validation on a request body annotated with @Valid fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationErrorsException(MethodArgumentNotValidException ex,HttpServletRequest request) {
        logger.error("[GlobalExceptionHandler] MethodArgumentNotValidException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        errors.put("path", request.getRequestURI());
        errors.put("timestamp", Instant.now().toString());
        errors.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

       // Add field-specific validation errors
       ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return responseBuilder.error(HttpStatus.BAD_REQUEST, errors, "Validation failed for request body");
   }

    //When the request body is missing or contains malformed/invalid JSON that cannot be deserialized
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("[GlobalExceptionHandler] HttpMessageNotReadableException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, null, "Invalid or malformed JSON request body");
    }

    //Request parameter or path variable can't be converted to the required data type
    //Invalid type in @RequestParam or @PathVariable.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("[GlobalExceptionHandler] MethodArgumentTypeMismatchException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, null, "Invalid type for parameter: " + ex.getName());
    }

    //Request using an HTTP method (GET, POST, PUT, DELETE, etc.) that is not supported by the endpoint
    //Wrong HTTP method (e.g., POST on GET endpoint).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("[GlobalExceptionHandler] HttpRequestMethodNotSupportedException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.METHOD_NOT_ALLOWED, null, "HTTP method not supported for this endpoint");
    }

    //Content-Type (e.g., application/json, multipart/form-data, etc.) of the request is not supported by the API endpoint
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        logger.error("[GlobalExceptionHandler] HttpMediaTypeNotSupportedException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, "Content-Type not supported");
    }

    //If email is null in authentication context
    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleUnauthorizedException(Unauthorized ex) {
        logger.error("[GlobalExceptionHandler] Unauthorized ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.UNAUTHORIZED, null, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Map<String,Object>>> handleBadRequestException(BadRequestException ex) {
        logger.error("[GlobalExceptionHandler] BadRequestException ExceptionType: {}  | Message: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, null, ex.getMessage());
    }

}
