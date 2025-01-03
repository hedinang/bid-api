# Use an official OpenJDK runtime as a parent image
FROM khipu/openjdk17-alpine

# Set the working directory
WORKDIR /bid-api

# Copy the JAR file into the container
COPY jks.jks ssl/jks.jks
COPY keystore.p12 ssl/keystore.p12
COPY target/bid-api-0.0.1-SNAPSHOT.jar target.jar

# Run the JAR file
CMD ["java", "-jar", "target.jar"]