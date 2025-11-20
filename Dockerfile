FROM maven:3.9-eclipse-temurin-23

WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk

COPY pom.xml .
COPY src ./src
COPY redis-server-java.sh .

RUN chmod +x redis-server-java.sh && \
    mkdir -p /root/.sdkman/candidates/java && \
    ln -s /opt/java/openjdk /root/.sdkman/candidates/java/current

CMD ["mvn", "test"]
