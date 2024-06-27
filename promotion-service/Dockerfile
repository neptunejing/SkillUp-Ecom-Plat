# 基础镜像
FROM openjdk:17-jdk-slim

# 维护者信息
LABEL maintainer="jingnanqing@hotmail.com"

# 设置工作目录
WORKDIR /usr/app

# 将应用程序的 JAR 文件复制到容器中
COPY target/promotion-service-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8003

# 运行 Spring Boot 应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]