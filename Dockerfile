FROM eclipse-temurin:21-alpine

ADD /libs/jenv-zgw-portal-backend-1.0.jar /opt/app.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/opt/app.jar"]