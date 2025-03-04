package com.zky.domain.activity.service.partake;

import com.zky.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.zky.domain.activity.model.entity.*;
import com.zky.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description:
 * @create: 2025/3/4
 */
@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{


    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-mm");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-mm-dd");


    public RaffleActivityPartakeService(IActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate) {
        //1.查询总账户额度
        ActivityAccountEntity activityAccountEntity = activityRepository.queryActivityAccountByUserId(userId, activityId);
        //1.2额度判断
        if(null == activityAccountEntity || activityAccountEntity.getTotalCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }

        String month = dateFormatMonth.format(currentDate);
        String day = dateFormatDay.format(currentDate);

        //2.查询月账户额度
        ActivityAccountMonthEntity activityAccountMonthEntity = activityRepository.queryActivityAccountMonthByUserId(userId, activityId);
        //2.1额度判断
        if(null != activityAccountMonthEntity && activityAccountMonthEntity.getMonthCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        //2.2月账户是否存在判断
        boolean isExistAccountMonth = null != activityAccountMonthEntity;
        if(null == activityAccountMonthEntity){
            //不存在则新增
            activityAccountMonthEntity = new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthCount(activityAccountEntity.getMonthCount());
            activityAccountMonthEntity.setMonthCountSurplus(activityAccountEntity.getMonthCountSurplus());
        }

        //3查询日账户额度
        ActivityAccountDayEntity activityAccountDayEntity = activityRepository.queryActivityAccountDayByUserId(userId, activityId);
        //3.1额度判断
        if(null != activityAccountDayEntity && activityAccountDayEntity.getDayCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        //3.2日账户是否存在判断
        boolean isExistAccountDay = null != activityAccountDayEntity;
        if(null == activityAccountDayEntity){
            //不存在则新增
            activityAccountDayEntity = new ActivityAccountDayEntity();
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDay(month);
            activityAccountDayEntity.setDayCount(activityAccountEntity.getMonthCount());
            activityAccountDayEntity.setDayCountSurplus(activityAccountEntity.getMonthCountSurplus());
        }

        //4.构建聚合对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = new CreatePartakeOrderAggregate();
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setExistAccountMonth(isExistAccountMonth);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonthEntity);
        createPartakeOrderAggregate.setExistAccountDay(isExistAccountDay);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDayEntity);

        return createPartakeOrderAggregate;
    }

    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate) {
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        UserRaffleOrderEntity userRaffleOrder = new UserRaffleOrderEntity();
        userRaffleOrder.setUserId(userId);
        userRaffleOrder.setActivityId(activityId);
        userRaffleOrder.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrder.setActivityName(activityEntity.getActivityName());
        userRaffleOrder.setOrderId(RandomStringUtils.randomNumeric(12));
        userRaffleOrder.setOrderTime(currentDate);
        userRaffleOrder.setOrderState(UserRaffleOrderStateVO.create);
        return userRaffleOrder;
    }
}
