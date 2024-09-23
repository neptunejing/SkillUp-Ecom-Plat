for port in $(seq 6379 6384); \
do \
   docker stop redis-${port}
   docker rm redis-${port}
done
