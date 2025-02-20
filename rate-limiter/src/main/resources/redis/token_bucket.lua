local key = KEYS[1] -- Redis key
local rate = tonumber(ARGV[2])  -- token 生成速率
local capacity = tonumber(ARGV[1]) -- 桶容量
local now = tonumber(ARGV[3]) -- 当前时间戳
local requested = 1 -- 每次请求消耗一个 token

-- 获取当前剩余 token 数，如果为空则设置为最大容量
local last_tokens = tonumber(redis.call("HGET", key, "tokens")) or capacity
-- 获取上次更新时间，如果为空则设置为当前时间
local last_time = tonumber(redis.call("HGET", key, "last_time")) or now

-- 计算时间差
local delta = math.max(0, now - last_time)
-- 根据时间差和速率计算期间应该应该生成的令牌数，不得超出桶容量
local new_tokens = math.min(capacity, last_tokens + delta * rate)

-- 处理请求
local allowed = false
if new_tokens >= requested then -- 如果令牌足够
    new_tokens = new_tokens - requested -- 消耗令牌
    allowed = true -- 允许请求
end

-- 更新 token 数和最后更新时间
redis.call("HMSET", key, "tokens", new_tokens, "last_time", now)
-- 设置自动过期时间为填满桶所需时间的两倍
redis.call("EXPIRE", key, math.ceil(capacity / rate) * 2)

return allowed and 1 or 0
