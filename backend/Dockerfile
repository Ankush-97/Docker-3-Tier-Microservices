FROM eclipse-temurin:17-jdk

WORKDIR /app

ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar mysql-connector.jar

COPY App.java .
RUN javac App.java

EXPOSE 8080

CMD ["java", "-cp", ".:mysql-connector.jar", "App"]
