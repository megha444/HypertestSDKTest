FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/hypertest-1.0.0.jar /app/hypertest-1.0.0.jar

ENV HT_MODE=RECORD
ENV DB_HOST=db_host
ENV DB_PORT=db_port
ENV DB_USER=db_user
ENV DB_PASS=db_pass
ENV DB_NAME=db_name
ENV HTTP_ENDPOINT=http_endpoint


EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/agent-1.0.0.jar", "-jar", "/app/hypertest-1.0.0.jar"]
