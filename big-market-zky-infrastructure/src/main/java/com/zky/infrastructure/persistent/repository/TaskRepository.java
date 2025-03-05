package com.zky.infrastructure.persistent.repository;

import com.zky.domain.task.model.entity.TaskEntity;
import com.zky.domain.task.repository.ITaskRepository;
import com.zky.infrastructure.event.EventPublisher;
import com.zky.infrastructure.persistent.dao.ITaskDao;
import com.zky.infrastructure.persistent.po.Task;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/5
 */
@Repository
public class TaskRepository implements ITaskRepository {
    @Resource
    private ITaskDao taskDao;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        List<Task> tasks = taskDao.queryNoSendMessageTaskList();
        List<TaskEntity> taskEntities = new ArrayList<>(tasks.size());
        for(Task task : tasks){
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(task.getUserId());
            taskEntity.setTopic(task.getTopic());
            taskEntity.setMessage(task.getMessage());
            taskEntity.setMessageId(task.getMessageId());
            taskEntities.add(taskEntity);
        }
        return taskEntities;
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        //发送MQ消息
        eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());

    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskDao.updateTaskSendMessageCompleted(task);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskDao.updateTaskSendMessageFail(task);
    }
}
