## 介绍

基于 Spring Boot 开发的微服务秒杀系统。

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

### 数据库

项目使用 flyway 作为数据库迁移工具，仅需要创建数据库，建表语句位于各服务 `src/main/resources/db/migration` 路径下，随服务启动自动创建。

```sql
CREATE DATABASE IF NOT EXISTS `user-service`;
CREATE DATABASE IF NOT EXISTS `commodity-service`;
CREATE DATABASE IF NOT EXISTS `promotion-service`;
CREATE DATABASE IF NOT EXISTS `order-service-1`;
CREATE DATABASE IF NOT EXISTS `order-service-2`;
```

将上述语句创建为本地脚本 `~/docker/mysql/sql/init-db.sql`。

通过 `docker-compose.env.yml` 启动环境依赖时，该脚本会在 MySQL 容器第一次启动时执行。

### Redis 集群

创建 docker 网络

```shell
docker network create skillup_net
```

在项目根路径下执行 `sh start.sh`，执行后会在 6379～6384 启动 6 个 Redis 实例（关闭则执行 `sh stop.sh`），进入其中任何一个容器：

```shell
# 进入 redis-6379
docker exec -it redis-6379 bash
```

创建集群：
```shell
# 节点在 skillup_net 下的 IP
redis-cli --cluster create 172.19.0.2:6379 172.19.0.3:6380 172.19.0.4:6381 172.19.0.5:6382 172.19.0.6:6383 172.19.0.7:6384 --cluster-replicas 1
```

### Rocket MQ 配置
在 `~/docker/rocketmq/broker/conf` 创建 `broker.conf`，该配置会在执行 `docker-compose.env.yml` 时使用：

```shell
# 所属集群名称，如果节点较多可以配置多个
brokerClusterName = DefaultCluster
# broker 名称，master 和 slave 使用相同的名称，表明他们的主从关系
brokerName = broker1
#0 表示 Master，大于 0 表示不同的 slave
brokerId = 0
# 表示几点做消息删除动作，默认是凌晨 4 点
deleteWhen = 04
# 在磁盘上保留消息的时长，单位是小时
fileReservedTime = 48
# 有三个值：SYNC_MASTER，ASYNC_MASTER，SLAVE；同步和异步表示 Master 和 Slave 之间同步数据的机制；
brokerRole = ASYNC_MASTER
# 刷盘策略，取值为：ASYNC_FLUSH，SYNC_FLUSH 表示同步刷盘和异步刷盘；SYNC_FLUSH 消息写入磁盘后才返回成功状态，ASYNC_FLUSH 不需要；
flushDiskType = ASYNC_FLUSH
# 设置 broker 节点所在服务器的 ip 地址（** 这个非常重要，主从模式下，从节点会根据主节点的 brokerIP2 来同步数据，如果不配置，主从无法同步，brokerIP1 设置为自己外网能访问的 ip，服务器双网卡情况下必须配置，比如阿里云这种，主节点需要配置 ip1 和 ip2，从节点只需要配置 ip1 即可）
# 宿主机 IP
brokerIP1 = 192.168.31.96
# nameServer 地址，分号分割
namesrvAddr = rmqnamesrv:9876
# Broker 对外服务的监听端口，
listenPort = 10911
# 是否允许 Broker 自动创建 Topic
autoCreateTopicEnable = true
# 是否允许 Broker 自动创建订阅组
autoCreateSubscriptionGroup = true
# linux 开启 epoll
useEpollNativeSelector = true
```

---

## 运行

### 配置修改

- 修改 IP：**重要**，需要修改的概率不小
  - 宿主机 IP 会发生更改，此时需要修改 `broker.conf` 的 `brokerIP1` 字段；其次需要修改 `redis-cluster.tmpl` 中的 `cluster-announce-ip`
  - 容器虚拟 IP 会发生更改，此时执行 `redis-cli --cluster create` 创建集群时的 IP 按容器要按实际填写

- 用到 MySQL 的服务需要修改 `application.yaml`，`application.prod.yaml`，`docker-compose.env.yml` 的 MySQL 密码
- 修改 `docker-compose.service.yml` 中 user-service 环境变量的时区 `TZ=Europe/Helsinki` 为实际时区（否则容器化运行时，前端设置 cookies 可能直接过期）

### 启动环境服务

运行前确认 Redis 集群已经就绪:

```shell
# 启动集群节点
sh start.sh

# 关闭集群节点
sh stop.sh
```

执行下方 docker-compose 语句启动其他服务：

```shell
# 启动环境依赖
docker-compose -f docker-compose.env.yml up -d

# 关闭环境依赖
docker-compose -f docker-compose.env.yml down
```

### 启动业务服务

本地启动按照 IDE 提示操作。

容器启动则执行脚本：
```
# 启动业务服务
docker-compose -f docker-compose.service.yml up -d

# 退出业务服务
docker-compose -f docker-compose.service.yml down
```

启动后进入 Consul 控制台 [localhost:8500/ui/](localhost:8500/ui/) 查看服务启动情况，然后通过 [127.0.0.1:8010/promotion_activity.html](127.0.0.1:8010/promotion_activity.html) 下单。

---
## 修改

当修改代码后，需要重新 `package` 项目或者单个服务，停止服务端的所有容器，然后**删除旧的 image**，重新执行 `docker-compose.service.yml` 上传新的 image。