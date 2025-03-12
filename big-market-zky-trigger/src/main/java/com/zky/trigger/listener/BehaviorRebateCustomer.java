package com.zky.trigger.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangKaiYuan
 * @description: 用户行为返利接收MQ消息
 * @create: 2025/3/12
 */
@Slf4j
@Component
public class BehaviorRebateCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue(value = "send_rebate"))
    public void listener(String message) {
        try{
            log.info("监听用户行为返利发送消息，topic:{} message:{}", topic, message);
        }catch (Exception e){
            log.error("监听用户行为返利发送消息，消息失败。topic:{} message:{}", topic, message);
            throw e;
        }
    }
}
