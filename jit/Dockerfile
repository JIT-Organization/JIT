# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build output (assumes the JAR is in 'build/libs/')
COPY build/libs/*.jar app.jar

# Expose the application's port (optional, match your application's configuration)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
