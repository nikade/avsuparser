FROM openjdk:11-jre
ADD ./build/libs/avsuparser-1.0-SNAPSHOT.jar /app/service.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/app/service.jar"]
