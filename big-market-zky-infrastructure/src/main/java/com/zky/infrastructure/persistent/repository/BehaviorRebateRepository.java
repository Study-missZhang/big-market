package com.zky.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.zky.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.zky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.zky.domain.rebate.model.entity.TaskEntity;
import com.zky.domain.rebate.model.valobj.BehaviorTypeVO;
import com.zky.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.zky.domain.rebate.repository.IBehaviorRebateRepository;
import com.zky.infrastructure.event.EventPublisher;
import com.zky.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import com.zky.infrastructure.persistent.dao.ITaskDao;
import com.zky.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import com.zky.infrastructure.persistent.po.DailyBehaviorRebate;
import com.zky.infrastructure.persistent.po.Task;
import com.zky.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import com.zky.types.model.Response;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 行为返利服务仓储实现类
 * @create: 2025/3/12
 */
@Slf4j
@Component
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private IDailyBehaviorRebateDao dailyBehaviorRebateDao;
    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO) {
        List<DailyBehaviorRebate> dailyBehaviorRebates = dailyBehaviorRebateDao.queryDailyBehaviorRebateByBehaviorType(behaviorTypeVO.getCode());
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOList = new ArrayList<>(dailyBehaviorRebates.size());
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebates){
            dailyBehaviorRebateVOList.add(DailyBehaviorRebateVO.builder()
                    .behaviorType(dailyBehaviorRebate.getBehaviorType())
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(dailyBehaviorRebate.getRebateType())
                    .build());
        }
        return dailyBehaviorRebateVOList;
    }

    @Override
    public void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates) {
        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    for(BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates){
                        BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                        //构建返利流水订单对象
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());
                        //写入库中
                        userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);

                        //构建task记录，写入到库中
                        TaskEntity taskEntity = behaviorRebateAggregate.getTask();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setState(taskEntity.getState().getCode());
                        taskDao.insert(task);
                    }
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.info("写入返利记录，唯一索引冲突 userId:{}", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }

        //同步发送MQ消息
        for(BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates){
            TaskEntity taskEntity = behaviorRebateAggregate.getTask();
            Task task = new Task();
            task.setUserId(taskEntity.getUserId());
            task.setMessageId(taskEntity.getMessageId());
            try{
                //发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                //更新数据库记录，task任务表
                taskDao.updateTaskSendMessageCompleted(task);
            }catch (Exception e){
                log.info("写入返利记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }
    }
}
