package com.zky.domain.task.service;

import com.zky.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 消息任务服务接口
 * @create: 2025/3/5
 */
public interface ITaskService {
    /**
     * 查询发送失败的MQ消息和超时1分钟还没有发出去的MQ消息
     * @return
     */
    List<TaskEntity> queryNosSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
