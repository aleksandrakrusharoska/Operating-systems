FROM openjdk:11-jdk
WORKDIR /app
COPY UDPServer.java /app
RUN javac UDPServer.java
CMD ["java", "UDPServer"]