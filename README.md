## 介绍

基于 Spring Boot 开发的微服务秒杀系统。

### 技术栈

- Spring Boot
- MySQL
- Redis： 
    - 预热阶段缓存热点数据「可用库存」
    - 存储 `(orderId, operationName)` 记录实现接口幂等
- RocketMQ：
    - 异步化修改数据库库存实现高并发下削峰
    - 事务消息尽可能维护 MySQL 和 Redis 一致性
    - 延时消息进行超时未支付检查
- Consul：服务注册中心
- Gateway：微服务网关

### 服务划分

| 服务       | 模块                | 职责                                                                                |
|----------|-------------------|-----------------------------------------------------------------------------------|
| 用户服务     | user-service      | 用户注册、登录                                                                           |
| 商品服务     | commodity-service | 商品信息                                                                              |
| 秒杀商品服务   | promotion-service | 维护秒杀商品信息<br/> 负责预热，在 Redis 缓存可用库存<br/>维护可用库存操作流水，保证事务消息消费幂等                       |
| 下单服务 1.x | order-service-1.x | 处理 userId 最后一位数为奇数的用户订单，连接 order-service-1 库                                      |
| 下单服务 2.x | order-service-2.x | 处理 userId 最后一位数为偶数的用户订单，连接 order-service-2 库                                      |
| 网关服务     | gateway-service   | 在 8090 端口接收对各个服务的请求并转发<br/>通过谓词判断 Request Header 实现下单请求分流 order-service-1.x 或 2.x |



---

## 环境准备

### 克隆仓库

```
git clone https://github.com/neptunejing/SkillUp-Ecom-Plat.git
```

### 数据库初始化

项目使用 flyway 作为数据库迁移工具，仅需要创建数据库，建表语句位于各服务 `src/main/resources/db/migration` 路径下，随服务启动自动创建。

#### 本地运行

手动创建库：

```sql
CREATE DATABASE IF NOT EXISTS `user-service`;
CREATE DATABASE IF NOT EXISTS `commodity-service`;
CREATE DATABASE IF NOT EXISTS `promotion-service`;
CREATE DATABASE IF NOT EXISTS `order-service-1`;
CREATE DATABASE IF NOT EXISTS `order-service-2`;
```

#### 容器化运行

将上述语句创建为本地脚本 `~/docker/mysql/sql/init-db.sql`。

通过 `docker-compose.env.yml` 启动环境依赖时，该脚本会在 MySQL 容器第一次启动时执行。

---

## 运行

### 修改

- 用到 MySQL 的服务需要修改 `application.yaml` 和 `application.prod.yaml` 的 MySQL 密码
- 修改 `docker-compose.env.yml` 中的 MySQL 密码
- 修改 `docker-compose.service.yml` 中 user-service 环境变量的时区 `TZ=Europe/Helsinki` 为实际时区（否则容器化运行时，前端设置 cookies 可能直接过期）

### 本地运行

运行前确认 MySQL、Redis、RocketMQ、Consul 已经就绪。

推荐按照下列顺序启动微服务：

- gateway-servive、user-service、commodity-service、promotion-service
- order-service 1.x、order-service 2.x
- frontend-service

### 容器化运行

运行前确认 MySQL、Redis、RocketMQ、Consul 容器已经就绪。

执行下方 docker-compose 语句：

```shell
# 启动环境依赖
docker-compose -f docker-compose.env.yml up -d
# 启动业务服务
docker-compose -f docker-compose.service.yml up -d
```

启动后进入 Consul 控制台 localhost:8500/ui/ 查看服务启动情况，然后通过 127.0.0.1:8010/promotion_activity.html 下单。