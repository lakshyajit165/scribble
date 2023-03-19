package com.scribble.apigateway.config;

import com.scribble.apigateway.dto.GenericResponseDTO;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/** source ref:
 * youtube: https://www.youtube.com/watch?v=qSSwSCEIo0I
 * github: https://github.com/ThomasVitale/spring-cloud-gateway-webinar-feb-2021
 */
@Configuration
public class FallbackConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.GET("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.POST("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.PUT("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.PATCH("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.DELETE("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.OPTIONS("/note-service/fallback"),
                        this::handleNoteServiceFallback)
                .andRoute(RequestPredicates.HEAD("/note-service/fallback"),
                        this::handleNoteServiceFallback)

                .andRoute(RequestPredicates.POST("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.GET("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.PUT("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.PATCH("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.DELETE("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.OPTIONS("/auth-service/fallback"),
                        this::handleAuthServiceFallback)
                .andRoute(RequestPredicates.HEAD("/auth-service/fallback"),
                        this::handleAuthServiceFallback);
    }

    @NonNull
    public Mono<ServerResponse> handleNoteServiceFallback(ServerRequest request) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(APPLICATION_JSON).body(BodyInserters
                        .fromValue(new GenericResponseDTO("Note service is taking too long to respond!")));
    }

    @NonNull
    public Mono<ServerResponse> handleAuthServiceFallback(ServerRequest request) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(APPLICATION_JSON).body(BodyInserters
                        .fromValue(new GenericResponseDTO("Auth service is taking too long to respond!")));
    }

}