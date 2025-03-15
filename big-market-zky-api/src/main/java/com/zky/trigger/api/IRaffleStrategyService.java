package com.zky.trigger.api;

import com.zky.trigger.api.dto.*;
import com.zky.types.model.Response;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖服务接口
 * @create: 2025/2/16
 */
public interface IRaffleStrategyService {

    /**
     * 装配策略接口
     * @param strategyId 策略ID
     * @return 装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询奖品列表接口
     * @param requestDTO 抽奖奖品列表请求
     * @return 奖品列表响应
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO 奖品请求参数
     * @return 奖品响应对象
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO requestDTO);

    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖品范围
     * @param request 请求对象
     * @return 权重奖品配置列表「这里会返回全部，前端可按需展示一条已达标的，或者一条要达标的」
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO request);
}
