FROM openjdk:23
COPY ./out/artifacts/task5_jar/task5.jar app-1.0.0.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app-1.0.0.jar" ]
