package com.zky.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/20
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ZookeeperTest {

    @Resource
    private CuratorFramework curatorFramework;

    /**
     * 创建临时节点
     */
    @Test
    public void createEphemeralNode() throws Exception {
        String path = "/big-market-dcc/config/downgradeSwitch/test/a";
        String data = "0";
        if(null == curatorFramework.checkExists().forPath(path)){
            curatorFramework.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    /**
     * 往节点中设置数据
     */
    @Test
    public void setData() throws Exception {
        curatorFramework.setData().forPath("/big-market-dcc/config/downgradeSwitch", "111".getBytes(StandardCharsets.UTF_8));

        //获取数据
        String res = new String(curatorFramework.getData().forPath("/big-market-dcc/config/downgradeSwitch"), StandardCharsets.UTF_8);
        log.info("测试结果: {}", res);
    }
}
