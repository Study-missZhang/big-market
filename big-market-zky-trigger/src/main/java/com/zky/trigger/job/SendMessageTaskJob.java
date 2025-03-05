package com.zky.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.zky.domain.task.model.entity.TaskEntity;
import com.zky.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: ZhangKaiYuan
 * @description: 发送MQ消息任务队列
 * @create: 2025/3/5
 */
@Component()
@Slf4j
public class SendMessageTaskJob {
    @Resource
    private ITaskService taskService;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try {
            //获取分库的数量
            int count = dbRouter.dbCount();

            //逐个库扫描表【每个库都有一个任务表】
            for (int dbIdx= 1; dbIdx <= count; dbIdx++){
                int finalDbIdx = dbIdx;
                executor.execute(() -> {
                    try {
                        dbRouter.setDBKey(finalDbIdx);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNosSendMessageTaskList();
                        if(taskEntities.isEmpty()) return;

                        //发送MQ消息
                        for (TaskEntity taskEntity : taskEntities){
                            //开启线程发送，提高发送效率。配置的线程池策略为 CallerRunsPolicy，在 ThreadPoolConfig 配置中有4个策略，面试中容易对比提问。可以检索下相关资料。
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                }catch (Exception e){
                                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                                }
                            });
                        }
                    }finally {
                        dbRouter.clear();
                    }
                });
            }
        }catch (Exception e){
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouter.clear();
        }
    }
}
