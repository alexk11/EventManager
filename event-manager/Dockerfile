# Используем минимальный runtime-образ с JDK 21
FROM eclipse-temurin:21-jre-alpine
# Задаем переменную окружения с названием jar-файла, можно переопределить при сборке
ARG JAR_FILE=target/event-manager-0.0.1-SNAPSHOT.jar
# Создаём рабочую директорию приложения
WORKDIR /app
# Копируем jar-файл в контейнер
COPY ${JAR_FILE} app.jar
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
# Открываем порт для доступа к приложению
EXPOSE 8082