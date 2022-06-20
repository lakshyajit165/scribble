package com.scribble.noteservice.middlewares;

import com.scribble.noteservice.dto.ApiErrorDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * source: https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
 * Github: https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-web-rest/src/main/java/com/baeldung/errorhandling/CustomRestExceptionHandler.java
 * */
@RestControllerAdvice
public class ApiRequestErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestErrorHandler.class);

    // 400

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiErrorDTO apiErrorDTO = new ApiErrorDTO("There are validation errors", errors);
        return handleExceptionInternal(ex, apiErrorDTO, headers, HttpStatus.BAD_REQUEST, request);
    }

    // 404

    @Override
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

    @Override
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
