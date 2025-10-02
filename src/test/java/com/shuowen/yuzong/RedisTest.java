//package com.shuowen.yuzong;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import java.util.List;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import com.shuowen.yuzong.redis.RedisDao;
//
//@SpringBootTest
//public class RedisTest
//{
//    @Autowired
//    private RedisDao redisDao;
//
//    private static final String TEST_KEY = "test:key";
//    private static final String TEST_HASH_KEY = "test:hash";
//    private static final String TEST_LIST_KEY = "test:list";
//    private static final String TEST_SET_KEY = "test:set";
//    private static final String TEST_ZSET_KEY = "test:zset";
//
//    @BeforeEach
//    @AfterEach
//    void cleanup()
//    {
//        redisDao.remove(TEST_KEY, TEST_HASH_KEY, TEST_LIST_KEY, TEST_SET_KEY, TEST_ZSET_KEY);
//        System.out.println("✅ 已清理测试 Key");
//        System.out.println("----------------------------------------");
//    }
//
//    @Test
//    void testSetAndGet()
//    {
//        System.out.println("=== 测试基础存储与读取 ===");
//        // 测试普通存储
//        redisDao.set(TEST_KEY, "Hello Redis");
//        System.out.println("📥 存储数据: Key=" + TEST_KEY + ", Value=Hello Redis");
//        String value = (String) redisDao.get(TEST_KEY);
//        System.out.println("📤 读取数据: Key=" + TEST_KEY + ", Value=" + value);
//        Assertions.assertEquals("Hello Redis", value);
//
//        // 测试带过期时间的存储
//        redisDao.set(TEST_KEY, "Expire in 10s", 10L);
//        System.out.println("⏳ 存储数据（10秒过期）: Key=" + TEST_KEY);
//        Assertions.assertTrue(redisDao.exists(TEST_KEY), "Key 应存在");
//        System.out.println("🔍 检查 Key 是否存在: " + TEST_KEY + " → 存在");
//    }
//
//    @Test
//    void testDelete()
//    {
//        System.out.println("=== 测试删除操作 ===");
//        redisDao.set(TEST_KEY, "To be deleted");
//        System.out.println("📥 存储待删除数据: Key=" + TEST_KEY);
//
//        redisDao.remove(TEST_KEY);
//        System.out.println("🗑️ 删除 Key: " + TEST_KEY);
//
//        boolean exists = redisDao.exists(TEST_KEY);
//        System.out.println("🔍 检查 Key 是否存在: " + TEST_KEY + " → " + (exists ? "存在" : "不存在"));
//        Assertions.assertFalse(exists);
//    }
//
//    @Test
//    void testHashOperations()
//    {
//        System.out.println("=== 测试哈希操作 ===");
//        redisDao.hmSet(TEST_HASH_KEY, "name", "John");
//        redisDao.hmSet(TEST_HASH_KEY, "age", 30);
//        System.out.println("📥 存储哈希数据: Key=" + TEST_HASH_KEY + ", Fields=[name, age]");
//
//        String name = (String) redisDao.hmGet(TEST_HASH_KEY, "name");
//        int age = (Integer) redisDao.hmGet(TEST_HASH_KEY, "age");
//        System.out.println("📤 读取哈希字段: name=" + name + ", age=" + age);
//
//        Assertions.assertEquals("John", name);
//        Assertions.assertEquals(30, age);
//    }
//
//    @Test
//    void testListOperations()
//    {
//        System.out.println("=== 测试列表操作 ===");
//        redisDao.lPush(TEST_LIST_KEY, "Java");
//        redisDao.lPush(TEST_LIST_KEY, "Redis");
//        System.out.println("📥 向列表添加元素: [Java, Redis]");
//
//        List<Object> list = redisDao.lRange(TEST_LIST_KEY, 0, -1);
//        System.out.println("📤 读取列表内容: " + list);
//
//        Assertions.assertEquals(2, list.size());
//        Assertions.assertEquals("Java", list.get(0));
//        Assertions.assertEquals("Redis", list.get(1));
//    }
//
//    @Test
//    void testSetOperations()
//    {
//        System.out.println("=== 测试集合操作 ===");
//        redisDao.addSet(TEST_SET_KEY, "Apple");
//        redisDao.addSet(TEST_SET_KEY, "Banana");
//        System.out.println("📥 向集合添加元素: [Apple, Banana]");
//
//        boolean exists = redisDao.isMember(TEST_SET_KEY, "Apple");
//        System.out.println("🔍 检查元素是否存在: Apple → " + exists);
//        Assertions.assertTrue(exists);
//
//        Set<Object> members = redisDao.setMembers(TEST_SET_KEY);
//        System.out.println("📤 集合所有成员: " + members);
//        Assertions.assertEquals(2, members.size());
//    }
//
//    @Test
//    void testZSetOperations()
//    {
//        System.out.println("=== 测试有序集合操作 ===");
//        redisDao.zAdd(TEST_ZSET_KEY, "PlayerA", 95.5);
//        redisDao.zAdd(TEST_ZSET_KEY, "PlayerB", 88.0);
//        System.out.println("📥 添加有序集合元素: PlayerA(95.5), PlayerB(88.0)");
//
//        Set<Object> range = redisDao.rangeByScore(TEST_ZSET_KEY, 85.0, 100.0);
//        System.out.println("🔢 按分数范围查询(85-100): " + range);
//        Assertions.assertEquals(2, range.size());
//
//        Set<Object> ascRank = redisDao.range(TEST_ZSET_KEY, 0L, 1L);
//        System.out.println("⬆️ 按升序排名查询: " + ascRank);
//        Assertions.assertTrue(ascRank.contains("PlayerB")); // 分数低的在前
//    }
//}
