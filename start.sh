DOCKER_PATH="$HOME/docker/redis"

# 创建宿主机配置
for port in $(seq 6379 6384);
do
  mkdir -p $DOCKER_PATH/node-${port}/conf \
  && port=${port} envsubst < redis-cluster.tmpl > $DOCKER_PATH/node-${port}/conf/redis.conf \
  && mkdir -p $DOCKER_PATH/node-${port}/data;\
done

# 启动 redis 容器
for port in $(seq 6379 6384); \
do \
  docker run -it -d -p ${port}:${port} -p 1${port}:1${port} \
  -v $DOCKER_PATH/node-${port}/conf/redis.conf:/usr/local/etc/redis/redis.conf \
  -v $DOCKER_PATH/node-${port}/data:/data \
  --privileged=true --restart always --name redis-${port} --net skillup_net \
  --sysctl net.core.somaxconn=1024 redis redis-server /usr/local/etc/redis/redis.conf
done