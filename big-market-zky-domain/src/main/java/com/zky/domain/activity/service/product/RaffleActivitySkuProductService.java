package com.zky.domain.activity.service.product;

import com.zky.domain.activity.model.entity.SkuProductEntity;
import com.zky.domain.activity.repository.IActivityRepository;
import com.zky.domain.activity.service.IRaffleActivitySkuProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description sku商品服务
 * @create 2024-06-15 09:30
 */
@Service
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Resource
    private IActivityRepository repository;

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {
        return repository.querySkuProductEntityListByActivityId(activityId);
    }

}
