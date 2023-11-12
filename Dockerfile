FROM openjdk:17
COPY build/libs/user-1.0.0.jar user-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/user-1.0.0.jar"]