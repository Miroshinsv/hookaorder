FROM gradle:jdk18-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon

FROM openjdk:20-slim-buster

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*-boot.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions","-jar","/app/spring-boot-application.jar"]