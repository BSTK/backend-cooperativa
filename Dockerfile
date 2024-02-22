FROM adoptopenjdk/openjdk11
EXPOSE 8080
ARG JAR_FILE=target/backend-cooperativa.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
