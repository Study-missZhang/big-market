package com.zky.trigger.job;

import com.zky.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.zky.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/2/13
 */
@Slf4j
@Component()
public class UpdateAwardStockJob {
    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try {
            //log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if(strategyAwardStockKeyVO == null) return;
            //log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e) {
            //log.error("定时任务，更新奖品消耗库存失败", e);
        }
    }
}
