FROM alpine:latest as build

ADD https://download.java.net/java/early_access/alpine/15/binaries/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz /opt/jdk/
RUN tar -xzvf /opt/jdk/openjdk-14-ea+15_linux-x64-musl_bin.tar.gz -C /opt/jdk/

RUN ["/opt/jdk/jdk-14/bin/jlink", "--compress=2", \
     "--module-path", "/opt/jdk/jdk-14/jmods/", \
     "--add-modules", "java.base,java.instrument,java.xml", \
     "--output", "/jlinked"]

FROM alpine:latest as transjawi
COPY --from=build /jlinked /opt/jdk/
ADD target/transjawi-1.0.0.jar /app/
CMD ["/opt/jdk/bin/java", "-jar", "/app/transjawi-1.0.0.jar"]
