package com.zky.domain.task.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangKaiYuan
 * @description: 任务实体对象
 * @create: 2025/3/5
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
    private String message;
}
