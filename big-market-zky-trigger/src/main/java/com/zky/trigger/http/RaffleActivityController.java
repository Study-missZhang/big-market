package com.zky.trigger.http;

import com.alibaba.fastjson.JSON;
import com.zky.domain.activity.model.entity.*;
import com.zky.domain.activity.model.valobj.OrderTradeTypeVO;
import com.zky.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.zky.domain.activity.service.IRaffleActivityPartakeService;
import com.zky.domain.activity.service.IRaffleActivitySkuProductService;
import com.zky.domain.activity.service.armory.IActivityArmory;
import com.zky.domain.award.model.entity.UserAwardRecordEntity;
import com.zky.domain.award.model.valobj.AwardStateVO;
import com.zky.domain.award.service.IAwardService;
import com.zky.domain.credit.model.entity.CreditAccountEntity;
import com.zky.domain.credit.model.entity.TradeEntity;
import com.zky.domain.credit.model.valobj.TradeNameVO;
import com.zky.domain.credit.model.valobj.TradeTypeVO;
import com.zky.domain.credit.service.ICreditAdjustService;
import com.zky.domain.rebate.model.entity.BehaviorEntity;
import com.zky.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.zky.domain.rebate.model.valobj.BehaviorTypeVO;
import com.zky.domain.rebate.service.IBehaviorRebateService;
import com.zky.domain.strategy.model.entity.RaffleAwardEntity;
import com.zky.domain.strategy.model.entity.RaffleFactorEntity;
import com.zky.domain.strategy.service.IRaffleStrategy;
import com.zky.domain.strategy.service.armory.IStrategyArmory;
import com.zky.trigger.api.IRaffleActivityService;
import com.zky.trigger.api.dto.*;
import com.zky.types.enums.ResponseCode;
import com.zky.types.exception.AppException;
import com.zky.types.model.Response;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动服务 注意；在不引用 application/case 层的时候，就需要让接口实现层来做领域的串联。一些较大规模的系统，需要加入 case 层。
 * @create: 2025/3/6
 */
@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleActivityController implements IRaffleActivityService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private IAwardService awardService;
    @Resource
    private IBehaviorRebateService behaviorRebateService;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditAdjustService creditAdjustService;
    @Resource
    private IRaffleActivitySkuProductService raffleActivitySkuProductService;

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     *
     * curl --request GET \
     *   --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */
    @RequestMapping(value = "armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId) {
        //装配预热
        try {
            //活动装配
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            activityArmory.assembleActivitySkuByActivityId(activityId);
            //2.策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return response;
        }catch (Exception e){
            log.info("活动装配，数据预热，失败 activityId:{}", activityId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     *
     * curl --request POST \
     *   --url http://localhost:8091/api/v1/raffle/activity/draw \
     *   --header 'content-type: application/json' \
     *   --data '{
     *     "userId":"xiaofuge",
     *     "activityId": 100301
     * }'
     */
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try {
            log.info("活动抽奖 userId:{} activityId:{}", request.getUserId(), request.getActivityId());
            //1.参数校验
            if(StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.参与活动 - 创建抽奖订单
            UserRaffleOrderEntity orderEntity = raffleActivityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("活动抽奖，创建订单 userId:{} activityId:{} orderId:{}", request.getUserId(), request.getActivityId(), orderEntity.getOrderId());
            //3.抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(orderEntity.getUserId())
                    .strategyId(orderEntity.getStrategyId())
                    .endDateTime(orderEntity.getEndDateTime())
                    .build());
            //4.存放结果 - 写入中奖结果
            UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                    .userId(orderEntity.getUserId())
                    .activityId(orderEntity.getActivityId())
                    .strategyId(orderEntity.getStrategyId())
                    .orderId(orderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVO.create)
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    .build();

            awardService.saveUserAwardRecord(userAwardRecord);
            //5.返回用户中奖结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        }catch (AppException e){
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();

        }catch (Exception e){
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到返利结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate">/api/v1/raffle/activity/calendar_sign_rebate</a>
     * 入参：xiaofuge
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate -d "userId=xiaofuge" -H "Content-Type: application/x-www-form-urlencoded"
     */
    @RequestMapping(value = "calendar_sign_rebate", method = RequestMethod.POST)
    @Override
    public Response<Boolean> calendarSignRebate(@RequestParam String userId) {
        try {
            log.info("日历签到返利开始 userId:{}", userId);
            BehaviorEntity behaviorEntity = new BehaviorEntity();
            behaviorEntity.setUserId(userId);
            behaviorEntity.setBehaviorTypeVO(BehaviorTypeVO.SIGN);
            behaviorEntity.setOutBusinessNo(dateFormat.format(new Date()));
            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            log.info("日历签到返利完成 userId:{} orderIds:{}", userId, JSON.toJSONString(orderIds));
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();

        }catch (AppException e){
            log.info("日历签到返利异常 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.info("日历签到返利失败 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    /**
     * 判断是否完成日历签到返利接口
     * @param userId 用户ID
     * @return 签到是否成功
     *
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/is_calendar_sign_rebate -d "userId=xiaofuge" -H "Content-Type: application/x-www-form-urlencoded"
     */
    @RequestMapping(value = "is_calendar_sign_rebate", method = RequestMethod.POST)
    @Override
    public Response<Boolean> isCalendarSignRebate(@RequestParam String userId) {
        //1.调用Rebate接口中查询数据库方法，查看outBusinessNo是否有值。
        try {
            log.info("查询用户是否完成日历签到返利开始 userId:{}", userId);
            String outBusinessNo = dateFormat.format(new Date());
            List<BehaviorRebateOrderEntity> behaviorRebateOrderEntities = behaviorRebateService.queryOrderByOutBusinessNo(userId, outBusinessNo);
            log.info("查询用户是否完成日历签到返利完成 userId:{} size:{}", userId, behaviorRebateOrderEntities.size());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(!behaviorRebateOrderEntities.isEmpty()) //只要不为空，就证明签到成功
                    .build();
        }catch (Exception e){
            log.info("查询用户是否完成日历签到返利失败 userId:{}", userId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }


    /**
     * 查询账户额度
     * @param request 用户请求对象
     * @return
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/query_user_activity_account \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"xiaofuge",
     * "activityId": 100301
     * }'
     */
    @RequestMapping(value = "query_user_activity_account", method = RequestMethod.POST)
    @Override
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(@RequestBody UserActivityAccountRequestDTO request) {
        try {
            log.info("查询账户额度开始， userId:{}, activityId:{}", request.getUserId(), request.getActivityId());
            //1.参数校验
            if(StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            ActivityAccountEntity activityAccountEntity = raffleActivityAccountQuotaService.queryRaffleActivityAccount(request.getUserId(), request.getActivityId());
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = UserActivityAccountResponseDTO.builder()
                        .totalCount(activityAccountEntity.getTotalCount())
                        .totalCountSurplus(activityAccountEntity.getTotalCountSurplus())
                        .dayCount(activityAccountEntity.getDayCount())
                        .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                        .monthCount(activityAccountEntity.getMonthCount())
                        .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                        .build();
            log.info("查询账户额度完成， userId:{}, activityId:{}, dto:{}", request.getUserId(), request.getActivityId(), JSON.toJSONString(userActivityAccountResponseDTO));
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();
        }catch (Exception e){
            log.info("查询账户额度失败， userId:{}, activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "credit_pay_exchange_sku", method = RequestMethod.POST)
    @Override
    public Response<Boolean> creditPayExchangeSku(@RequestBody SkuProductShopCartRequestDTO request) {
        try{
            log.info("积分兑换商品开始 userId:{} sku:{}", request.getUserId(), request.getSku());
            //1.创建兑换商品sku订单，outBusinessNo 每次创建出一个单号。
            UnpaidActivityOrderEntity unpaidActivityOrder = raffleActivityAccountQuotaService.createSkuRechargeOrder(SkuRechargeEntity.builder()
                    .userId(request.getUserId())
                    .sku(request.getSku())
                    .outBusinessNo(RandomStringUtils.randomNumeric(12))
                    .orderTradeTypeVO(OrderTradeTypeVO.credit_pay_trade)
                    .build());
            log.info("积分兑换商品，创建订单完成 userId:{} sku:{} outBusinessNo:{}", request.getUserId(), request.getSku(), unpaidActivityOrder.getOutBusinessNo());

            //2.支付兑换商品
            String orderId = creditAdjustService.createOrder(TradeEntity.builder()
                    .userId(unpaidActivityOrder.getUserId())
                    .tradeName(TradeNameVO.CONVERT_SKU)
                    .tradeType(TradeTypeVO.REVERSE)
                    .amount(unpaidActivityOrder.getPayAmount().negate())
                    .outBusinessNo(unpaidActivityOrder.getOutBusinessNo())
                    .build());
            log.info("积分兑换商品，支付订单完成  userId:{} sku:{} orderId:{}", request.getUserId(), request.getSku(), orderId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        }catch (Exception e){
            log.error("积分兑换商品失败 userId:{} sku:{}", request.getUserId(), request.getSku(), e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

    @RequestMapping(value = "query_sku_product_list_by_activity_id", method = RequestMethod.POST)
    @Override
    public Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId) {
        try {
            log.info("查询sku商品集合开始 activityId:{}", activityId);
            // 1. 参数校验
            if (null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 查询商品&封装数据
            List<SkuProductEntity> skuProductEntities = raffleActivitySkuProductService.querySkuProductEntityListByActivityId(activityId);
            List<SkuProductResponseDTO> skuProductResponseDTOS = new ArrayList<>(skuProductEntities.size());
            for (SkuProductEntity skuProductEntity : skuProductEntities) {

                SkuProductResponseDTO.ActivityCount activityCount = new SkuProductResponseDTO.ActivityCount();
                activityCount.setTotalCount(skuProductEntity.getActivityCount().getTotalCount());
                activityCount.setMonthCount(skuProductEntity.getActivityCount().getMonthCount());
                activityCount.setDayCount(skuProductEntity.getActivityCount().getDayCount());

                SkuProductResponseDTO skuProductResponseDTO = new SkuProductResponseDTO();
                skuProductResponseDTO.setSku(skuProductEntity.getSku());
                skuProductResponseDTO.setActivityId(skuProductEntity.getActivityId());
                skuProductResponseDTO.setActivityCountId(skuProductEntity.getActivityCountId());
                skuProductResponseDTO.setStockCount(skuProductEntity.getStockCount());
                skuProductResponseDTO.setStockCountSurplus(skuProductEntity.getStockCountSurplus());
                skuProductResponseDTO.setProductAmount(skuProductEntity.getProductAmount());
                skuProductResponseDTO.setActivityCount(activityCount);
                skuProductResponseDTOS.add(skuProductResponseDTO);
            }

            log.info("查询sku商品集合完成 activityId:{} skuProductResponseDTOS:{}", activityId, JSON.toJSONString(skuProductResponseDTOS));
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(skuProductResponseDTOS)
                    .build();
        } catch (Exception e) {
            log.error("查询sku商品集合失败 activityId:{}", activityId, e);
            return Response.<List<SkuProductResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_user_credit_account", method = RequestMethod.POST)
    @Override
    public Response<BigDecimal> queryUserCreditAccount(String userId) {
        try {
            log.info("查询用户积分值开始 userId:{}", userId);
            CreditAccountEntity creditAccountEntity = creditAdjustService.queryUserCreditAccount(userId);
            log.info("查询用户积分值完成 userId:{} adjustAmount:{}", userId, creditAccountEntity.getAdjustAmount());
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(creditAccountEntity.getAdjustAmount())
                    .build();
        } catch (Exception e) {
            log.error("查询用户积分值失败 userId:{}", userId, e);
            return Response.<BigDecimal>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


}
