package com.zky.trigger.api;

import com.zky.trigger.api.dto.RaffleAwardListRequestDTO;
import com.zky.trigger.api.dto.RaffleAwardListResponseDTO;
import com.zky.trigger.api.dto.RaffleStrategyRequestDTO;
import com.zky.trigger.api.dto.RaffleStrategyResponseDTO;
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
}
