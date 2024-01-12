-- 1. check if promotion exists
-- KEYS[1]: PROMOTION_promotionId_STOCK
if redis.call('exists', KEYS[1]) == 1 then
    -- 2. get key and value
    local stock = tonumber(redis.call('get', KEYS[1]));
    if (stock > 0) then
        -- 3. stock > 0 and lock stock
        redis.call('set', KEYS[1], stock - 1);
        return stock - 1;
    end
    -- sold out
    return -1;
end
-- 4. invalid
return -2;

