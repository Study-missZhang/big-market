package com.zky.domain.task.repository;

import com.zky.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/5
 */
public interface ITaskRepository {
    List<TaskEntity> queryNoSendMessageTaskList();


    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
