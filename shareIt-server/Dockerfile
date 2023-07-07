FROM amazoncorretto:11-alpine-jdk
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/schema.sql /docker-entrypoint-initdb.d/
RUN apk add postgresql && apk add postgresql-client
ENTRYPOINT ["sh", "-c", "sleep 10 && PGPASSWORD=purrrrrr psql --host=db -p 5432 -U kitty -d shareit -f /docker-entrypoint-initdb.d/schema.sql && java ${JAVA_OPTS} -jar /app.jar"]