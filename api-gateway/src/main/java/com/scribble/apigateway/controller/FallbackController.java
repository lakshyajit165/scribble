package com.scribble.apigateway.controller;

import com.scribble.apigateway.dto.GenericResponseDTO;
import com.scribble.apigateway.model.GatewayErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/auth-service/fallback")
    public ResponseEntity<?> authServiceFallBackMethod() {
        return ResponseEntity.status(503).body(new GenericResponseDTO(GatewayErrorCode.SERVICE_UNAVAILABLE, "Auth service is taking longer than expected. Please try again later."));
    }

    @GetMapping("/note-service/fallback")
    public ResponseEntity<?> noteServiceFallBackMethod() {
        return ResponseEntity.status(503).body(new GenericResponseDTO(GatewayErrorCode.SERVICE_UNAVAILABLE, "Note Service is taking longer than expected. Please try again later."));
    }
}
