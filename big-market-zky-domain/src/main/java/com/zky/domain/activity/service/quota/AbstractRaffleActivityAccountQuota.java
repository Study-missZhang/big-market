package com.zky.domain.activity.service.quota;

import com.zky.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.zky.domain.activity.model.entity.*;
import com.zky.domain.activity.model.valobj.OrderTradeTypeVO;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zky.domain.activity.service.quota.policy.ITradePolicy;
import com.zky.domain.activity.service.quota.rule.IActionChain;
import com.zky.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动抽象类，定义标准的流程
 * @create: 2025/2/26
 */
@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {

    private final Map<String, ITradePolicy> tradePolicyGroup;

    public AbstractRaffleActivityAccountQuota(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory, Map<String, ITradePolicy> tradePolicyGroup) {
        super(activityRepository, defaultActivityChainFactory);
        this.tradePolicyGroup = tradePolicyGroup;
    }

    @Override
    public UnpaidActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        // 1. 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 查询未支付订单「一个月以内的未支付订单」
        UnpaidActivityOrderEntity unpaidCreditOrder =  activityRepository.queryUnpaidActivityOrder(skuRechargeEntity);
        if (null != unpaidCreditOrder) return unpaidCreditOrder;

        // 3. 查询基础信息
        // 3.1 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        // 3.2 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3.3 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 4. 账户额度 【交易属性的兑换，需要校验额度账户】
        if (OrderTradeTypeVO.credit_pay_trade.equals(skuRechargeEntity.getOrderTradeTypeVO())){
            BigDecimal availableAmount = activityRepository.queryUserCreditAccountAmount(userId);
            if (availableAmount.compareTo(activitySkuEntity.getProductAmount()) < 0) {
                throw new AppException(ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getCode(), ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getInfo());
            }
        }


        // 5. 构建订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);

        // 6. 交易策略 - 【积分兑换，支付类订单】【返利无支付交易订单，直接充值到账】【订单状态变更交易类型策略】
        ITradePolicy tradePolicy = tradePolicyGroup.get(skuRechargeEntity.getOrderTradeTypeVO().getCode());
        tradePolicy.trade(createQuotaOrderAggregate);

        // 7. 返回单号
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        return UnpaidActivityOrderEntity.builder()
                .userId(userId)
                .orderId(activityOrderEntity.getOrderId())
                .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                .payAmount(activityOrderEntity.getPayAmount())
                .build();
    }

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
