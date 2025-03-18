package com.zky.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.zky.domain.activity.model.entity.SkuRechargeEntity;
import com.zky.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zky.domain.credit.model.aggregate.TradeAggregate;
import com.zky.domain.credit.model.entity.TradeEntity;
import com.zky.domain.credit.model.valobj.TradeNameVO;
import com.zky.domain.credit.model.valobj.TradeTypeVO;
import com.zky.domain.credit.service.ICreditAdjustService;
import com.zky.domain.rebate.event.SendRebateMessageEvent;
import com.zky.domain.rebate.model.valobj.RebateTypeVO;
import com.zky.types.enums.ResponseCode;
import com.zky.types.event.BaseEvent;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author: ZhangKaiYuan
 * @description: 用户行为返利接收MQ消息
 * @create: 2025/3/12
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditAdjustService creditAdjustService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try{
            log.info("监听用户行为返利发送消息，topic:{} message:{}", topic, message);
            //1.转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();

            //2.入账奖励
            switch (rebateMessage.getRebateType()){
                case "sku":
                    SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                    skuRechargeEntity.setUserId(rebateMessage.getUserId());
                    skuRechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
                    skuRechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    raffleActivityAccountQuotaService.createSkuRechargeOrder(skuRechargeEntity);
                    break;
                case "integral":
                    TradeEntity tradeEntity = new TradeEntity();
                    tradeEntity.setUserId(rebateMessage.getUserId());
                    tradeEntity.setTradeName(TradeNameVO.REBATE);
                    tradeEntity.setTradeType(TradeTypeVO.FORWARD);
                    tradeEntity.setAmount(new BigDecimal(rebateMessage.getRebateConfig()));
                    tradeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    creditAdjustService.createOrder(tradeEntity);
                    break;
            }
        }catch (AppException e) {
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())) {
                log.warn("监听用户行为返利消息，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        }catch (Exception e){
            log.error("监听用户行为返利发送消息，消息失败。topic:{} message:{}", topic, message);
            throw e;
        }
    }
}
