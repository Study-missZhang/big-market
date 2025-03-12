package com.zky.domain.rebate.service;

import com.zky.domain.award.model.valobj.TaskStateVO;
import com.zky.domain.rebate.event.SendRebateMessageEvent;
import com.zky.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.zky.domain.rebate.model.entity.BehaviorEntity;
import com.zky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.zky.domain.rebate.model.entity.TaskEntity;
import com.zky.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.zky.domain.rebate.repository.IBehaviorRebateRepository;
import com.zky.types.common.Constants;
import com.zky.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 行为返利服务实现类
 * @create: 2025/3/12
 */
@Service
public class BehaviorRebateService implements IBehaviorRebateService{

    @Resource
    IBehaviorRebateRepository behaviorRebateRepository;

    @Resource
    SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        //1.查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOs= behaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVO());
        if(null == dailyBehaviorRebateVOs || dailyBehaviorRebateVOs.isEmpty()) return new ArrayList<>();

        //2.构建聚合对象
        List<String> orderIds = new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>();
        for(DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOs){
            //拼装唯一的业务ID: 用户ID_返利类型_外部透彻业务ID
            String bizId = behaviorEntity.getUserId() + Constants.UNDERLINE + dailyBehaviorRebateVO.getRebateType() + Constants.UNDERLINE + behaviorEntity.getOutBusinessNo();
            //返利订单对象构建
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .bizId(bizId)
                    .build();
            orderIds.add(behaviorRebateOrderEntity.getOrderId());

            //MQ 消息对象
            SendRebateMessageEvent.RebateMessage rebateMessage = new SendRebateMessageEvent.RebateMessage();
            rebateMessage.setUserId(behaviorEntity.getUserId());
            rebateMessage.setRebateType(behaviorRebateOrderEntity.getRebateType());
            rebateMessage.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
            rebateMessage.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
            rebateMessage.setBizId(bizId);

            //构建消息对象
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage = sendRebateMessageEvent.buildEventMessage(rebateMessage);

            //Task对象构建
            TaskEntity task = new TaskEntity();
            task.setUserId(behaviorEntity.getUserId());
            task.setTopic(sendRebateMessageEvent.topic());
            task.setMessageId(rebateMessageEventMessage.getId());
            task.setMessage(rebateMessageEventMessage);
            task.setState(TaskStateVO.create);

            //聚合对象存入
            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .task(task)
                    .build();
            behaviorRebateAggregates.add(behaviorRebateAggregate);
        }

        //3.存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateAggregates);

        //4.返回订单集合对象
        return orderIds;
    }
}
