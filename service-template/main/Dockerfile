FROM amazoncorretto:11.0.14-al2 as builder
ARG JAR_FILE=build/libs/main-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# TODO RUN true - intermittent https://github.com/moby/moby/issues/37965

FROM amazoncorretto:11.0.14-al2

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

RUN yum install -y shadow-utils && \
  yum clean all && \
  rm -rf /var/cache/yum

RUN groupadd springboot && useradd --system -g springboot springboot
USER springboot

HEALTHCHECK --start-period=30s --interval=5s CMD curl -f http://localhost:8080/actuator/health || exit 1

COPY --from=builder dependencies/ ./
RUN true
COPY --from=builder snapshot-dependencies/ ./
RUN true
COPY --from=builder spring-boot-loader/ ./
RUN true
COPY --from=builder application/ ./
