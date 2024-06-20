-- check if promotion exists
-- KEYS[1]: PROMOTION_promotionId_STOCK
if redis.call('exists', KEYS[1]) == 1
then
    -- idempotence check
    if redis.call('exists', KEYS[2]) == 1 and redis.call('get', KEYS[2]) == ARGV[1]
    then
        return redis.call('get', KEYS[1]);
    end

    -- get key and value
    local stock = tonumber(redis.call('get', KEYS[1]));
    if (stock >= 0) then
        -- stock >= 0 and revert stock
        redis.call('set', KEYS[1], stock + 1);
        redis.call('set', KEYS[2], ARGV[1]);
        return stock + 1;
    end
end
-- 4. invalid
return -1;

