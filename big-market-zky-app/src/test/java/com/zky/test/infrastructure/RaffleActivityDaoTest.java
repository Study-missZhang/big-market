package com.zky.test.infrastructure;

import com.alibaba.fastjson2.JSON;
import com.zky.infrastructure.persistent.dao.IRaffleActivityDao;
import com.zky.infrastructure.persistent.po.RaffleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动表测试类
 * @create: 2025/2/25
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RaffleActivityDaoTest {

    @Resource
    private IRaffleActivityDao raffleActivityDao;

    @Test
    public void test_queryRaffleActivityByActivityId(){
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(100003L);
        log.info("测试结果：{}", JSON.toJSONString(raffleActivity));
    }
}
