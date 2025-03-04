package com.zky.domain.activity.service.partake;

import com.alibaba.fastjson.JSON;
import com.zky.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zky.domain.activity.model.entity.ActivityEntity;
import com.zky.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.zky.domain.activity.model.entity.UserRaffleOrderEntity;
import com.zky.domain.activity.model.valobj.ActivityStateVO;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.domain.activity.service.IRaffleActivityPartakeService;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动参与抽奖类
 * @create: 2025/3/3
 */
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected final IActivityRepository activityRepository;

    //使用构造方法的方式注入activityRepository
    public AbstractRaffleActivityPartake(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //0.获得基础信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date currentDate = new Date();

        //1.获得校验:状态，时间
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        if(!ActivityStateVO.open.equals(activityEntity.getState())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }
        if(activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        //2.判断是否存在未使用的订单
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUserRaffleOrder(partakeRaffleActivityEntity);
        if(null != userRaffleOrderEntity){
            //存在,直接返回活动订单对象
            log.info("创建参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }

        //3.额度账户过滤&返回账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilterAccount(userId, activityId, currentDate);

        //4.创建参与抽奖订单
        UserRaffleOrderEntity userRaffleOrder = this.buildUserRaffleOrder(userId, activityId, currentDate);

        //5.将订单对象填充到订单聚合对象
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        //6.保存聚合对象(事务写库) - 一个领域内的一个聚合是一个事务操作
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);
        return null;
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);

    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}
