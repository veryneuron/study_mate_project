FROM openjdk:17-oracle
WORKDIR /usr/src/app
COPY build/libs/studymate-api.jar .
COPY api_server_credential ./api_server_credential
EXPOSE 8000
ENTRYPOINT ["java","-jar","studymate-api.jar"]