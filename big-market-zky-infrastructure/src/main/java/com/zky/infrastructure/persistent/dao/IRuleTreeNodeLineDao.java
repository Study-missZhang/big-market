package com.zky.infrastructure.persistent.dao;

import com.zky.infrastructure.persistent.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: ZhangKaiYuan
 * @description: 规则树节点线 Dao
 * @create: 2025/2/10
 */
@Mapper
public interface IRuleTreeNodeLineDao {

    List<RuleTreeNodeLine> queryRuleTreeNodeLineListByTreeId(String treeId);
}
