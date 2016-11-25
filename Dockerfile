FROM openjdk:8

RUN apt-get clean
RUN apt-get update
RUN apt-get install -y gradle

RUN gradle

COPY build/libs/RoutenService-all-0.0.1.jar rs/jar/
COPY build.gradle rs/
COPY src/ rs/

CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "/rs/jar/RoutenService-all-0.0.1.jar"]