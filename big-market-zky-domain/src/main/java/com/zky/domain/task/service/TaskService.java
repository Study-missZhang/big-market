package com.zky.domain.task.service;

import com.zky.domain.task.model.entity.TaskEntity;
import com.zky.domain.task.repository.ITaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/5
 */
@Service
public class TaskService implements ITaskService{
    @Resource
    private ITaskRepository taskRepository;

    @Override
    public List<TaskEntity> queryNosSendMessageTaskList() {
        return taskRepository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        taskRepository.sendMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        taskRepository.updateTaskSendMessageCompleted(userId, messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        taskRepository.updateTaskSendMessageFail(userId, messageId);
    }
}
