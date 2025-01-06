# Use an official OpenJDK runtime as a parent image
FROM khipu/openjdk17-alpine

# Set the working directory
WORKDIR /bid-api

# Install Chromium and dependencies
RUN apk update && apk add --no-cache \
    chromium \
    chromium-chromedriver \
    libstdc++ \
    nss \
    freetype \
    harfbuzz \
    ttf-freefont

# Set environment variables for Chrome and Chromedriver
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROMEDRIVER=/usr/lib/chromium/chromedriver
ENV PATH=$PATH:/usr/lib/chromium/

# Copy the JAR file and other required files into the container
COPY jks.jks ssl/jks.jks
COPY keystore.p12 ssl/keystore.p12
COPY target/bid-api-0.0.1-SNAPSHOT.jar target.jar

# Verify Chromium and Chromedriver versions (optional, for debugging)
RUN chromium-browser --version && echo $CHROMEDRIVER

# Run the JAR file
CMD ["java", "-jar", "target.jar"]
