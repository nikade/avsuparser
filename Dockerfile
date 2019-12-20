FROM openjdk:11-jre
ADD ./build/libs/avsuparser-1.1.jar /app/service.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/app/service.jar"]
