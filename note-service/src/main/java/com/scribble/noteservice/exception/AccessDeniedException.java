package com.scribble.noteservice.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@NoArgsConstructor
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends Exception {
    private String resourceName;
    private String operation;
    public AccessDeniedException(String resourceName, String operation) {
        super(String.format("%s access denied for resource %s ", operation, resourceName));
        this.resourceName = resourceName;
    }
}
