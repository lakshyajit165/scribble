package com.scribble.apigateway.dto;

import com.scribble.apigateway.model.GatewayErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericResponseDTO {
    private GatewayErrorCode gatewayErrorCode;
    private String message;
}
