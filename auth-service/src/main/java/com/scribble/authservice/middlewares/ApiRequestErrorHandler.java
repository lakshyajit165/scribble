package com.scribble.authservice.middlewares;

import com.scribble.authservice.dto.ApiErrorDTO;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * source: https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
 * Github: https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-web-rest/src/main/java/com/baeldung/errorhandling/CustomRestExceptionHandler.java
 * */
@RestControllerAdvice
public class ApiRequestErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestErrorHandler.class);

    // 400
    @NonNull
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        errors.addAll(ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(ObjectError::getDefaultMessage).toList());

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO("There are validation errors", errors);
        return ResponseEntity.badRequest().body(apiErrorDTO);
    }

    // 404
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        final ApiErrorDTO apiErrorDTO = new ApiErrorDTO("No handler found! ", error);
        return new ResponseEntity<Object>(apiErrorDTO, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // 405
    @NonNull
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final ApiErrorDTO apiErrorDTO = new ApiErrorDTO("Method not supported!", builder.toString());
        return new ResponseEntity<Object>(apiErrorDTO, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

}
