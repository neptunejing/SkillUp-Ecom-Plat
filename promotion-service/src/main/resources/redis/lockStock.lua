-- KEYS [PROMOTION_promotionId_STOCK] [ORDER_ID] [OPERATION_NAME]
-- check if promotion exists
if redis.call('exists', KEYS[1]) == 1
then
    -- idempotence check
    if redis.call('exists', KEYS[2]) == 1 and redis.call('get', KEYS[2]) == KEYS[3]
    then
        return redis.call('get', KEYS[1]);
    end

    -- get key and value
    local stock = tonumber(redis.call('get', KEYS[1]));
    if (stock > 0)
    then
        -- stock > 0 and lock stock
        redis.call('set', KEYS[1], stock - 1);
        -- add a new (ORDER_ID, OPERATION_NAME)
        redis.call('set', KEYS[2], KEYS[3]);
        return stock - 1;
    end
    -- sold out
    return -1;
end
-- invalid
return -2;

