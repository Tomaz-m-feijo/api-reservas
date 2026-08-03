# Estágio 1: Build da aplicação (utilizando a imagem oficial do Gradle com JDK 21)
FROM gradle:9.5-jdk21 AS builder
WORKDIR /app

# Copia os arquivos de configuração do Gradle e o código-fonte
COPY build.gradle settings.gradle ./
COPY src ./src

# Executa o build gerando o executável .jar
# O parâmetro -x test pula a execução dos testes durante a construção da imagem
RUN gradle clean bootJar -x test

# Estágio 2: Construção da imagem final (utilizando a JRE enxuta do Alpine Linux)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Define o fuso horário para garantir que o OffsetDateTime local do servidor fique correto (ex: BRT)
ENV TZ=America/Sao_Paulo

# Expõe a porta padrão da aplicação
EXPOSE 8080

# Comando para iniciar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]