package com.zky.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.zky.domain.strategy.model.valobj.RuleTreeVO;
import com.zky.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/2/10
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyRepositoryTest {
    @Resource
    private IStrategyRepository strategyRepository;
    @Test
    public void queryRuleTreeVOByTreeId(){
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeByTreeId("tree_lock");
        log.info("测试结果：{}" + JSON.toJSONString(ruleTreeVO));
    }
}
