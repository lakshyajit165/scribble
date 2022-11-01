### Scribble Web App
> Scribble is a sample web application designed using spring cloud design pattern which consists of Spring Cloud Gateway for reverse-proxy and request filtering, Eureka Server for service discovery along with 2 other microservices - auth and notes.

#### Features implemented in this web app:
- Spring Cloud Gateway (as reverse proxy, load balancer and circuit breaker).
- AWS Cognito for authentication with refresh token in SpringBoot microservices along with JWT.
- Server side cookies.
- DB queries using predicates(Inside notes microservice for the search notes API)
- RestControllerAdvice as middleware for better handling of server side error messages.
- Refresh token implementation with http request interceptor in Angular.
- Angular route guards.
- Debouncing for search notes on the client side.
