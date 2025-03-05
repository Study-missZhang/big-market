package com.zky.domain.award.model.entity;

import com.zky.domain.award.event.SendAwardMessageEvent;
import com.zky.domain.award.model.valobj.TaskStateVO;
import com.zky.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 任务表实体对象
 * @create: 2025/3/4
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /** 用户ID **/
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息ID**/
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVO state;
}
