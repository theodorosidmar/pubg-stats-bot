FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/pubg-stats-bot.jar ./pubg-stats-bot.jar
CMD ["java", "-jar", "/app/pubg-stats-bot.jar"]
