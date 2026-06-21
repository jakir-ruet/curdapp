# Dockerfile - Production Ready
FROM eclipse-temurin:21-jre-alpine

# Install runtime dependencies
RUN apk add --no-cache curl tzdata bash && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# Create application user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy your application JAR
COPY curdapp/target/curdapp-0.0.1-SNAPSHOT.jar app.jar

# Copy uploads directory if exists
RUN mkdir -p /app/uploads/images

# Create necessary directories
RUN mkdir -p /app/uploads/images /app/logs && \
    chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8090/actuator/health || exit 1

# JVM Optimization for Production
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+ParallelRefProcEnabled \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=UTC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
