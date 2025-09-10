FROM maven:3.8.5-openjdk-17 AS build
# 设置工作目录
WORKDIR /app

COPY pom.xml .
COPY src ./src

# 打包应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 运行阶段：使用轻量级JRE镜像
FROM openjdk:17-jdk-slim

LABEL maintainer="243583762@qq.com"

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]