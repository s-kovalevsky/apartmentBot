FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY ./target/apartmentBot-jar-with-dependencies.jar .

EXPOSE 8000

ENTRYPOINT [ "java", "-jar", "apartmentBot-jar-with-dependencies.jar" ]