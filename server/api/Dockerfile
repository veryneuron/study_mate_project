FROM eclipse-temurin:17-jre-focal
WORKDIR /usr/src/app
COPY build/libs/studymate-api.jar .
EXPOSE 8000
ENTRYPOINT ["java","-jar","studymate-api.jar"]