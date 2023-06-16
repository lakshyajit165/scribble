### Scribble Web App
[![Build, test and push note-service to docker](https://github.com/lakshyajit165/scribble/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/lakshyajit165/scribble/actions/workflows/main.yml)
> Scribble is a sample web application designed using microservice architecture which consists of nginx for reverse-proxy and request filtering, along with 2 other microservices - auth and notes.

#### Features implemented in this web app:
- Nginx (as reverse proxy, api-gateway and circuit breaker).
- AWS Cognito for authentication with refresh token in SpringBoot microservices along with JWT.
- Server side cookies.
- DB queries using predicates(Inside notes microservice for the search notes API)
- RestControllerAdvice as middleware for better handling of server side error messages.
- Refresh token implementation with http request interceptor in Angular.
- Angular route guards.
- Debouncing for search notes on the client side.
- Testing jenkins pipeline1.
