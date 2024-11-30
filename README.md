### Scribble Web App

[![Build, test and push note-service to docker](https://github.com/lakshyajit165/scribble/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/lakshyajit165/scribble/actions/workflows/main.yml)

[![Build test and push auth-service to Docker](https://github.com/lakshyajit165/scribble/actions/workflows/auth-service.yml/badge.svg)](https://github.com/lakshyajit165/scribble/actions/workflows/auth-service.yml)

> Scribble is a sample web application designed using microservice architecture which consists of nginx for reverse-proxy and request filtering, along with 2 other microservices - auth and notes.

#### Features implemented in this web app:

-   Nginx (as reverse proxy, api-gateway and circuit breaker).
-   AWS Cognito for authentication with refresh token in SpringBoot microservices along with JWT.
-   Server side cookies(http only so that they are not modifiable on the client side)
-   DB queries using predicates(Inside notes microservice for the search notes API)
-   Server side pagination of search results.
-   RestControllerAdvice as middleware for better handling of server side error messages.
-   Refresh token implementation with http request interceptor in Angular.
-   Angular route guards.
-   Debouncing for search notes on the client side.
-   Unit testing for success and failure functionalities, for all the APIs.
-   Implemented github action script to build and push latest docker images when unit test execution is successful.
