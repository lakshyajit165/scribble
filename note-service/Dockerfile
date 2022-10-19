
# source: https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image/57145029#57145029
# First stage: JDK 11 with modules required for Spring Boot

FROM --platform=linux/amd64 amazoncorretto:11
COPY build/libs/note-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]