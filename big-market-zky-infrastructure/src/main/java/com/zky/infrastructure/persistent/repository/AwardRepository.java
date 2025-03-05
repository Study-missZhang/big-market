package com.zky.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.zky.domain.award.event.SendAwardMessageEvent;
import com.zky.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.zky.domain.award.model.entity.TaskEntity;
import com.zky.domain.award.model.entity.UserAwardRecordEntity;
import com.zky.domain.award.repository.IAwardRepository;
import com.zky.infrastructure.event.EventPublisher;
import com.zky.infrastructure.persistent.dao.ITaskDao;
import com.zky.infrastructure.persistent.dao.IUserAwardRecordDao;
import com.zky.infrastructure.persistent.po.Task;
import com.zky.infrastructure.persistent.po.UserAwardRecord;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @author: ZhangKaiYuan
 * @description: 奖品服务仓储
 * @create: 2025/3/4
 */
@Slf4j
@Repository
public class AwardRepository implements IAwardRepository {

    @Resource
    private IUserAwardRecordDao userAwardRecordDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;


    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        //根据实体对象，获取到持久化对象，然后进行sql操作。
        //获得信息
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();

        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userId);
        userAwardRecord.setActivityId(activityId);
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(awardId);
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task = new Task();
        task.setUserId(userId);
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        //事务进行sql操作
        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try{
                    //写入任务
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }

        //发送MQ消息
        try{
            eventPublisher.publish(task.getTopic(), task.getMessage());
            //更新task发送成功任务
            taskDao.updateTaskSendMessageCompleted(task);
        }catch (Exception e){
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            //消息发送失败，更新task发送失败任务
            taskDao.updateTaskSendMessageFail(task);
        }
    }
}
