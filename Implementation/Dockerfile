FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} implementation.jar
ENTRYPOINT ["java","-jar","/implementation.jar"]
