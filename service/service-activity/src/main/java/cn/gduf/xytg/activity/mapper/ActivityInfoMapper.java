package cn.gduf.xytg.activity.mapper;

import cn.gduf.xytg.model.activity.ActivityRule;
import cn.gduf.xytg.model.activity.ActivitySku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.gduf.xytg.model.activity.ActivityInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动信息Mapper
 * @date 2025/10/23 20:40
 */
@Repository
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {
    /**
     * 如果之前参加过，活动正在进行中，排除商品
     *
     * @param skuIdList
     * @return
     */
    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);

    /**
     * 根据skuId获取活动规则数据
     *
     * @param skuId
     * @return
     */
    List<ActivityRule> findActivityRule(@Param("skuId") Long skuId);

    /**
     * 获取购物车对应活动信息
     *
     * @param skuIdList
     * @return
     */
    List<ActivitySku> selectCartActivity(List<Long> skuIdList);
}
