package com.shuowen.yuzong.Tool.redis;


import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

@Component
public class RedisTool
{
    @Resource
    private RedisTemplate<String, Object> redis;  // 添加泛型类型

    private static final Logger logger = LoggerFactory.getLogger(RedisTool.class);

    // 统一异常处理的方法
    private <T> T executeRedisOperation(Supplier<T> operation, T defaultValue)
    {
        try
        {
            return operation.get();
        } catch (Exception e)
        {
            logger.error("Redis operation error: ", e);
            return defaultValue;
        }
    }

    private void executeRedisOperation(Runnable operation)
    {
        try
        {
            operation.run();
        } catch (Exception e)
        {
            logger.error("Redis operation error: ", e);
        }
    }

    // ==================== 字符串操作 ====================

    /**
     * 设置指定键值对
     */
    public boolean set(final String key, Object value)
    {
        return executeRedisOperation(() ->
        {
            redis.opsForValue().set(key, value);
            return true;
        }, false);
    }

    /**
     * 设置指定键值对，并设置过期时间
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit)
    {
        return executeRedisOperation(() ->
        {
            redis.opsForValue().set(key, value, expireTime, timeUnit);
            return true;
        }, false);
    }


    /**
     * 设置指定键值对，但是不主动覆盖
     *
     * @implNote 返回装箱是因为避免null错误
     */
    public Boolean setnx(final String key, Object value)
    {
        return executeRedisOperation(() -> redis.opsForValue().setIfAbsent(key, value), false);
    }


    /**
     * 设置键值对并且返回旧值
     */
    public Object getset(final String key, String value)
    {
        return executeRedisOperation(() -> redis.opsForValue().getAndSet(key, value), null);
    }

    /**
     * 获取指定键值对
     */
    public Object get(final String key)
    {
        ValueOperations<String, Object> operations = redis.opsForValue();
        return operations.get(key);
    }


    /**
     * 判断缓存中是否有对应的value
     */
    public boolean exists(final String key)
    {
        Boolean isExists = redis.hasKey(key);
        return BooleanUtils.isTrue(isExists);
    }


    /**
     * 删除对应的键值对
     */
    public void del(final String key)
    {
        if (exists(key))
        {
            redis.delete(key);
        }
    }

    /**
     * 批量删除对应的键值对
     */
    public void del(final String... keys)
    {
        for (String key : keys)
        {
            del(key);
        }
    }

    /**
     * 批量删除key
     */
    public void removePattern(final String pattern)
    {
        Set<String> keys = redis.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys))
        {
            redis.delete(keys);
        }
    }

    // ============================== 数字操作 ==============================


    /**
     * 数值增加1
     *
     * @return 增加后的值
     */
    public Long incr(final String key)
    {
        return executeRedisOperation(() -> redis.opsForValue().increment(key), null);
    }

    /**
     * 数值增加n
     *
     * @return 增加后的值
     */
    public Long incrBy(final String key, long increment)
    {
        return executeRedisOperation(() -> redis.opsForValue().increment(key, increment), null);
    }

    /**
     * 数值增加浮点数n
     *
     * @return 增加后的值
     */
    public Double incrByFloat(final String key, double increment)
    {

        return executeRedisOperation(() -> redis.opsForValue().increment(key, increment), null);
    }

    /**
     * 数值减少1
     *
     * @return 减少后的值
     */
    public Long decr(final String key)
    {
        return executeRedisOperation(() -> redis.opsForValue().decrement(key), null);
    }

    /**
     * 数值减少n
     *
     * @return 减少后的值
     */
    public Long decrBy(final String key, long decrement)
    {
        return executeRedisOperation(() -> redis.opsForValue().decrement(key, decrement), null);
    }


    // ============================== 列表操作 ==============================


    /**
     * 左侧插入
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long lpush(final String key, Object value)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPush(key, value), null);
    }

    /**
     * 左侧插入多个元素
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long lpush(final String key, Object... values)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPushAll(key, values), null);
    }

    /**
     * 左侧插入多个元素
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long lpush(final String key, Collection<Object> values)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPushAll(key, values), null);
    }

    /**
     * 右侧插入
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long rpush(final String key, Object value)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPush(key, value), null);
    }

    /**
     * 右侧插入多个元素
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long rpush(final String key, Object... values)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPushAll(key, values), null);
    }

    /**
     * 右侧插入多个元素
     *
     * @return 返回插入后列表的长度，失败返回null
     */
    public Long rpush(final String key, Collection<Object> values)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPushAll(key, values), null);
    }


    /**
     * 从列表左侧删除一个元素，并且把他返回
     *
     * @return 如果列表为空返回null
     */
    public Object lpop(final String key)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPop(key), null);
    }

    /**
     * 从列表左侧阻塞删除元素，并返回
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 如果超时返回null
     */
    public Object lpop(final String key, long timeout, TimeUnit unit)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPop(key, timeout, unit), null);
    }

    /**
     * 从列表右侧删除一个元素，并且把他返回
     *
     * @return 如果列表为空返回null
     */
    public Object rpop(final String key)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPop(key), null);
    }

    /**
     * 从列表右侧阻塞删除元素，并返回
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 如果超时返回null
     */
    public Object rpop(final String key, long timeout, TimeUnit unit)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPop(key, timeout, unit), null);
    }

    /**
     * 原子操作：从一个列表右侧弹出元素并插入到另一个列表左侧
     *
     * @param sourceKey      源列表键
     * @param destinationKey 目标列表键
     * @return 被移动的元素
     */
    public Object rpoplpush(final String sourceKey, final String destinationKey)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPopAndLeftPush(sourceKey, destinationKey), null);
    }

    /**
     * 阻塞版本的原子弹出插入操作
     *
     * @param sourceKey      源列表键
     * @param destinationKey 目标列表键
     * @param timeout        超时时间
     * @param unit           时间单位
     * @return 被移动的元素，如果超时返回null
     */
    public Object brpoplpush(final String sourceKey, final String destinationKey, long timeout, TimeUnit unit)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit), null);
    }

    /**
     * 获取列表长度
     *
     * @return 如果键不存在返回0
     */
    public Long llen(final String key)
    {
        return executeRedisOperation(() -> redis.opsForList().size(key), 0L);
    }

    /**
     * 根据索引获取列表元素
     *
     * @param index 索引位置（0表示第一个元素，-1表示最后一个元素）
     */
    public Object lindex(final String key, long index)
    {
        return executeRedisOperation(() -> redis.opsForList().index(key, index), null);
    }

    /**
     * 获取列表指定范围的元素
     *
     * @param start 起始索引（包含）
     * @param end   结束索引（包含）
     * @return 元素列表，如果键不存在返回空列表
     */
    public List<Object> lrange(final String key, long start, long end)
    {
        return executeRedisOperation(() -> redis.opsForList().range(key, start, end), Collections.emptyList());
    }

    /**
     * 获取列表全部元素
     */
    public List<Object> getl(final String key)
    {
        return lrange(key, 0, -1);
    }

    /**
     * 从列表中移除指定元素
     *
     * @param count 移除数量：
     *              count > 0: 从表头开始向表尾搜索，移除与value相等的元素，数量为count
     *              count < 0: 从表尾开始向表头搜索，移除与value相等的元素，数量为count的绝对值
     *              count = 0: 移除表中所有与value相等的值
     * @param value 要移除的值
     * @return 被移除元素的数量
     */
    public Long lrem(final String key, long count, Object value)
    {
        return executeRedisOperation(() -> redis.opsForList().remove(key, count, value), 0L);
    }

    /**
     * 设置列表指定索引位置的元素值
     *
     * @param index 索引位置
     * @param value 新的值
     */
    public void lset(final String key, long index, Object value)
    {
        executeRedisOperation(() -> redis.opsForList().set(key, index, value));
    }

    /**
     * 修剪列表，只保留指定范围内的元素
     *
     * @param start 起始索引
     * @param end   结束索引
     */
    public void ltrim(final String key, long start, long end)
    {
        executeRedisOperation(() -> redis.opsForList().trim(key, start, end));
    }

    /**
     * 在指定元素的第一次出现位置前，插入新元素
     *
     * @param key   列表的键
     * @param pivot 参考元素
     * @param value 要插入的新元素
     * @return 插入后列表的长度
     */
    public Long linsertBefore(final String key, Object pivot, Object value)
    {
        return executeRedisOperation(() -> redis.opsForList().leftPush(key, pivot, value), 0L);
    }

    /**
     * 在指定元素的第一次出现位置后，插入新元素
     *
     * @param key   列表的键
     * @param pivot 参考元素
     * @param value 要插入的新元素
     * @return 插入后列表的长度
     */
    public Long linsertAfter(final String key, Object pivot, Object value)
    {
        return executeRedisOperation(() -> redis.opsForList().rightPush(key, pivot, value), 0L);
    }


    /**
     * 只有当列表存在时，才从左侧插入元素，返回插入后列表的长度
     *
     * @return 如果列表不存在返回null
     */
    public Integer lpushx(final String key, Object value)
    {
        return executeRedisOperation(() ->
        {
            Long result = redis.opsForList().leftPushIfPresent(key, value);
            return result != null ? result.intValue() : null;
        }, null);
    }

    /**
     * 只有当列表存在时，才从右侧插入元素，返回插入后列表的长度
     *
     * @return 如果列表不存在返回null
     */
    public Integer rpushx(final String key, Object value)
    {
        return executeRedisOperation(() ->
        {
            Long result = redis.opsForList().rightPushIfPresent(key, value);
            return result != null ? result.intValue() : null;
        }, null);
    }


    // ============================== 键值对操作 ==============================

    /**
     * 设置哈希表中的字段值
     *
     * @return 插入失败返回false，而不是null
     */
    public boolean hset(final String key, final String field, Object value)
    {
        return executeRedisOperation(() ->
        {
            redis.opsForHash().put(key, field, value);
            return true;
        }, false);
    }

    /**
     * 批量设置哈希表中的字段值
     *
     * @return 插入失败返回false，而不是null
     */
    public boolean hset(final String key, final Map<String, Object> fieldValueMap)
    {
        return executeRedisOperation(() ->
        {
            redis.opsForHash().putAll(key, fieldValueMap);
            return true;
        }, false);
    }

    /**
     * 获取哈希表中指定字段的值
     */
    public Object hget(final String key, final String field)
    {
        return executeRedisOperation(() ->
                redis.opsForHash().get(key, field), null);
    }

    /**
     * 获取哈希表中多个字段的值
     */
    public List<Object> hmget(final String key, final Collection<String> fields)
    {
        return executeRedisOperation(() -> redis.opsForHash().multiGet(key, Collections.singleton(fields)), null);
    }

    /**
     * 获取哈希表中所有字段和值
     */
    public Map<Object, Object> hgetall(final String key)
    {
        return executeRedisOperation(() -> redis.opsForHash().entries(key), null);
    }

    /**
     * 删除哈希表中的一个或多个字段
     */
    public Long hdel(final String key, final Object... fields)
    {
        return executeRedisOperation(() -> redis.opsForHash().delete(key, fields), 0L);
    }

    /**
     * 判断哈希表中是否存在字段
     */
    public Boolean hexists(final String key, final String field)
    {
        return executeRedisOperation(() -> redis.opsForHash().hasKey(key, field), false);
    }

    /**
     * 获取哈希表中所有字段名
     */
    public Set<Object> hkeys(final String key)
    {
        return executeRedisOperation(() -> redis.opsForHash().keys(key), Collections.emptySet());
    }

    /**
     * 获取哈希表中所有值
     */
    public List<Object> hvalues(final String key)
    {
        return executeRedisOperation(() -> redis.opsForHash().values(key), null);
    }

    /**
     * 获取哈希表中字段的数量
     */
    public Long hlen(final String key)
    {
        return executeRedisOperation(() -> redis.opsForHash().size(key), 0L);
    }

    /**
     * 为哈希表中的数字字段值增加增量（整数）
     */
    public Long hincr(final String key, final String field, final long increment)
    {
        return executeRedisOperation(() -> redis.opsForHash().increment(key, field, increment), 0L);
    }

    /**
     * 为哈希表中的数字字段值增加增量（浮点数）
     */
    public Double hincr(final String key, final String field, final double increment)
    {
        return executeRedisOperation(() -> redis.opsForHash().increment(key, field, increment), 0.0);
    }

    /**
     * 如果字段不存在则设置哈希字段的值
     */
    public Boolean hsetnx(final String key, final String field, Object value)
    {
        return executeRedisOperation(() -> redis.opsForHash().putIfAbsent(key, field, value), false);
    }

    /**
     * 获取哈希字段值的字符串长度
     */
    public Long hstrlen(final String key, final String field)
    {
        return executeRedisOperation(() ->
        {
            Object value = redis.opsForHash().get(key, field);
            return value != null ? (long) value.toString().length() : 0L;
        }, 0L);
    }

    // ============================== 集合(Set)操作 ==============================

    /**
     * 向集合添加一个或多个成员
     */
    public Long sadd(final String key, final Object... values) {
        return executeRedisOperation(() -> redis.opsForSet().add(key, values), 0L);
    }

    /**
     * 获取集合中的所有成员
     */
    public Set<Object> smembers(final String key) {
        return executeRedisOperation(() -> redis.opsForSet().members(key), Collections.emptySet());
    }

    /**
     * 判断成员是否在集合中
     */
    public Boolean sismember(final String key, final Object value) {
        return executeRedisOperation(() -> redis.opsForSet().isMember(key, value), false);
    }

    /**
     * 获取集合的成员数量
     */
    public Long scard(final String key) {
        return executeRedisOperation(() -> redis.opsForSet().size(key), 0L);
    }

    /**
     * 移除并返回集合中的一个随机元素
     */
    public Object spop(final String key) {
        return executeRedisOperation(() -> redis.opsForSet().pop(key), null);
    }

    /**
     * 移除集合中的一个或多个成员
     */
    public Long srem(final String key, final Object... values) {
        return executeRedisOperation(() -> redis.opsForSet().remove(key, values), 0L);
    }

    /**
     * 随机返回集合中一个或多个成员
     */
    public Object srandmember(final String key) {
        return executeRedisOperation(() -> redis.opsForSet().randomMember(key), null);
    }

    /**
     * 随机返回集合中多个成员
     */
    public List<Object> srandmember(final String key, final long count) {
        return executeRedisOperation(() -> redis.opsForSet().randomMembers(key, count), Collections.emptyList());
    }

    /**
     * 返回多个集合的差集
     */
    public Set<Object> sdiff(final String... keys) {
        return executeRedisOperation(() -> redis.opsForSet().difference(List.of(keys)), Collections.emptySet());
    }

    /**
     * 返回多个集合的交集
     */
    public Set<Object> sinter(final String... keys) {
        return executeRedisOperation(() -> redis.opsForSet().intersect(List.of(keys)), Collections.emptySet());
    }

    /**
     * 返回多个集合的并集
     */
    public Set<Object> sunion(final String... keys) {
        return executeRedisOperation(() -> redis.opsForSet().union(List.of(keys)), Collections.emptySet());
    }

// ============================== 有序集合(ZSet)操作 ==============================

    /**
     * 向有序集合添加一个或多个成员，或更新已存在成员的分数
     */
    public Boolean zadd(final String key, final Object value, final double score) {
        return executeRedisOperation(() -> redis.opsForZSet().add(key, value, score), false);
    }

    /**
     * 批量添加有序集合成员
     */
    public Long zadd(final String key, final Map<Object, Double> valueScoreMap) {
        return executeRedisOperation(() -> {
            Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
            valueScoreMap.forEach((value, score) ->
                    tuples.add(ZSetOperations.TypedTuple.of(value, score)));
            return redis.opsForZSet().add(key, tuples);
        }, 0L);
    }

    /**
     * 获取有序集合的成员数量
     */
    public Long zcard(final String key) {
        return executeRedisOperation(() -> redis.opsForZSet().size(key), 0L);
    }

    /**
     * 获取有序集合中指定分数范围的成员数量
     */
    public Long zcount(final String key, final double min, final double max) {
        return executeRedisOperation(() -> redis.opsForZSet().count(key, min, max), 0L);
    }

    /**
     * 增加有序集合中成员的分数
     */
    public Double zincrby(final String key, final Object value, final double increment) {
        return executeRedisOperation(() -> redis.opsForZSet().incrementScore(key, value, increment), 0.0);
    }

    /**
     * 获取有序集合中指定范围的成员（按分数升序）
     */
    public Set<Object> zrange(final String key, final long start, final long end) {
        return executeRedisOperation(() -> redis.opsForZSet().range(key, start, end), Collections.emptySet());
    }

    /**
     * 获取有序集合中指定范围的成员（按分数降序）
     */
    public Set<Object> zrevrange(final String key, final long start, final long end) {
        return executeRedisOperation(() -> redis.opsForZSet().reverseRange(key, start, end), Collections.emptySet());
    }

    /**
     * 获取有序集合中指定分数范围的成员（按分数升序）
     */
    public Set<Object> zrangeByScore(final String key, final double min, final double max) {
        return executeRedisOperation(() -> redis.opsForZSet().rangeByScore(key, min, max), Collections.emptySet());
    }

    /**
     * 获取有序集合中指定分数范围的成员（按分数降序）
     */
    public Set<Object> zrevrangeByScore(final String key, final double min, final double max) {
        return executeRedisOperation(() -> redis.opsForZSet().reverseRangeByScore(key, min, max), Collections.emptySet());
    }

    /**
     * 获取有序集合中指定成员的排名（按分数升序，0开始）
     */
    public Long zrank(final String key, final Object value) {
        return executeRedisOperation(() -> redis.opsForZSet().rank(key, value), null);
    }

    /**
     * 获取有序集合中指定成员的排名（按分数降序，0开始）
     */
    public Long zrevrank(final String key, final Object value) {
        return executeRedisOperation(() -> redis.opsForZSet().reverseRank(key, value), null);
    }

    /**
     * 获取有序集合中指定成员的分数
     */
    public Double zscore(final String key, final Object value) {
        return executeRedisOperation(() -> redis.opsForZSet().score(key, value), null);
    }

    /**
     * 移除有序集合中的一个或多个成员
     */
    public Long zrem(final String key, final Object... values) {
        return executeRedisOperation(() -> redis.opsForZSet().remove(key, values), 0L);
    }

    /**
     * 移除有序集合中指定排名范围的成员
     */
    public Long zremrangeByRank(final String key, final long start, final long end) {
        return executeRedisOperation(() -> redis.opsForZSet().removeRange(key, start, end), 0L);
    }

    /**
     * 移除有序集合中指定分数范围的成员
     */
    public Long zremrangeByScore(final String key, final double min, final double max) {
        return executeRedisOperation(() -> redis.opsForZSet().removeRangeByScore(key, min, max), 0L);
    }

// ============================== HyperLogLog 操作 ==============================

    /**
     * 添加元素到 HyperLogLog
     */
    public Boolean pfadd(final String key, final Object... values) {
        return executeRedisOperation(() -> redis.opsForHyperLogLog().add(key, values) > 0, false);
    }

    /**
     * 获取 HyperLogLog 的基数估算值
     */
    public Long pfcount(final String key) {
        return executeRedisOperation(() -> redis.opsForHyperLogLog().size(key), 0L);
    }

    /**
     * 合并多个 HyperLogLog 到一个新的 HyperLogLog
     */
    public Boolean pfmerge(final String destKey, final String... sourceKeys) {
        return executeRedisOperation(() -> {
            redis.opsForHyperLogLog().union(destKey, sourceKeys);
            return true;
        }, false);
    }

// ============================== 通用操作补充 ==============================

    /**
     * 设置过期时间
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return executeRedisOperation(() -> redis.expire(key, timeout, unit), false);
    }

    /**
     * 获取过期时间
     */
    public Long ttl(final String key, final TimeUnit unit) {
        return executeRedisOperation(() -> redis.getExpire(key, unit), -2L);
    }

    /**
     * 移除过期时间，使键永久有效
     */
    public Boolean persist(final String key) {
        return executeRedisOperation(() -> redis.persist(key), false);
    }

    /**
     * 重命名键
     */
    public void rename(final String oldKey, final String newKey) {
        executeRedisOperation(() -> redis.rename(oldKey, newKey));
    }
}
