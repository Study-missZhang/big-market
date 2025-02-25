package com.zky.test.infrastructure;

import com.alibaba.fastjson2.JSON;
import com.zky.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import com.zky.infrastructure.persistent.po.RaffleActivityOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.jeasy.random.EasyRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/2/25
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityOrderDaoTest {
    @Resource
    IRaffleActivityOrderDao raffleActivityOrderDao;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    public void test_insert(){
        for (int i = 0; i < 50; i++) {
            RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
            //EasyRandom 随机生成对象值
            raffleActivityOrder.setUserId(easyRandom.nextObject(String.class));
            raffleActivityOrder.setActivityId(100301L);
            raffleActivityOrder.setActivityName("测试活动");
            raffleActivityOrder.setStrategyId(100006L);
            raffleActivityOrder.setOrderId(RandomStringUtils.randomNumeric(12));
            raffleActivityOrder.setOrderTime(new Date());
            raffleActivityOrder.setState("not_used");
            // 插入数据
            raffleActivityOrderDao.insert(raffleActivityOrder);
        }
    }

    @Test
    public void test_queryRaffleActivityOrderByUserId(){
        List<RaffleActivityOrder> llN = raffleActivityOrderDao.queryRaffleActivityOrderByUserId("LlN");
        log.info("测试结果：{}", JSON.toJSONString(llN));
    }
}
