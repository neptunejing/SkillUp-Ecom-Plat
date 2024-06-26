# 基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /usr/app

# 将应用程序的 JAR 文件复制到容器中
COPY target/order-service-1.2-1.0-SNAPSHOT.jar ./app.jar

# 暴露端口
EXPOSE 8014

# 运行 Spring Boot 应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]