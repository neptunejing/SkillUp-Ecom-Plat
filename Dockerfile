# 基础镜像
FROM openjdk:17-jdk-slim

# 维护者信息
LABEL maintainer="jingnanqing@hotmail.com"

# 将应用程序的 JAR 文件复制到容器中
COPY target/skillup-ecom-plat-1.0-SNAPSHOT.jar /usr/app/app.jar

# 设置工作目录
WORKDIR /usr/app

# 声明服务运行在 8080 端口
EXPOSE 8090

# 运行 Spring Boot 应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]