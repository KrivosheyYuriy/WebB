# Этап 1: Сборка приложения с помощью Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -Pprod  # Собираем приложение с профилем prod

# Этап 2: Создание образа Tomcat и копирование WAR-файла
FROM tomcat:latest

# Удаляем стандартное приложение Tomcat (опционально)
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем WAR-файл из этапа сборки
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war