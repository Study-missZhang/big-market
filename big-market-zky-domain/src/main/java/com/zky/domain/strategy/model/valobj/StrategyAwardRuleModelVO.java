package com.zky.domain.strategy.model.valobj;

import com.zky.domain.strategy.service.rule.factor.DefaultLogicFactory;
import com.zky.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * Date: 2025/1/15
 * 抽奖策略规则值对象；值对象，没有唯一ID，仅限从数据库中查询对象
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {
    private String ruleModels;
    /**
     * 获取抽奖中规则
     * @return
     */
    public String[] raffleCenterRuleModelList(){
       List<String> ruleModelList = new ArrayList<>();
       String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
       for (String ruleModelValue : ruleModelValues){
          if(DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)){
             ruleModelList.add(ruleModelValue);
          }
       }
       return ruleModelList.toArray(new String[0]);
    }
}
