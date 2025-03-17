package com.zky.domain.award.service.distribute.impl;

import com.zky.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.zky.domain.award.model.entity.DistributeAwardEntity;
import com.zky.domain.award.model.entity.UserAwardRecordEntity;
import com.zky.domain.award.model.entity.UserCreditAwardEntity;
import com.zky.domain.award.model.valobj.AwardStateVO;
import com.zky.domain.award.repository.IAwardRepository;
import com.zky.domain.award.service.distribute.IDistributeAward;
import com.zky.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author: ZhangKaiYuan
 * @description: 用户积分奖品，支持 award_config 透传，满足黑名单积分奖励。
 * @create: 2025/3/16
 */
@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    private IAwardRepository repository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        //获取到奖品ID
        Integer awardId = distributeAwardEntity.getAwardId();
        // 查询奖品配置信息 「优先走透传的随机积分奖品配置」
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if(StringUtils.isBlank(awardConfig)){
            awardConfig = repository.queryAwardConfig(awardId);
        }

        //拆分奖品配置信息：1,100  0,0.1
        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if(creditRange.length != 2){
            throw new RuntimeException("award_config 「" + awardConfig + "」配置不是一个范围值，如 1,100");
        }

        //生成随机积分值
        BigDecimal creditAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        //构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVO.complete
        );
        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(
                distributeAwardEntity.getUserId(),
                creditAmount
        );
        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userId(distributeAwardEntity.getUserId())
                .userAwardRecordEntity(userAwardRecordEntity)
                .userCreditAwardEntity(userCreditAwardEntity)
                .build();

        //存储发奖对象
        repository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);
    }

    /**
     * 随机获取min~max之间值
     * @param min 最小值
     * @param max 最大值
     * @return 随机获取之间值
     */
    private BigDecimal generateRandom(BigDecimal min, BigDecimal max){
        if(min.equals(max)) return min;
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.round(new MathContext(3));
    }
}
