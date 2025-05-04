FROM openjdk:11-jdk
WORKDIR /app
COPY UDPClient.java /app
RUN javac UDPClient.java
CMD ["java", "UDPClient"]