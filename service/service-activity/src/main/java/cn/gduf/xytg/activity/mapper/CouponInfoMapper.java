package cn.gduf.xytg.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.gduf.xytg.model.activity.CouponInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 优惠卷信息Mapper
 * @date 2025/10/23 20:38
 */
@Repository
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {
    /**
     * 根据优惠券id、分类id、用户id查询优惠券列表
     *
     * @param id
     * @param categoryId
     * @param userId
     * @return
     */
    List<CouponInfo> selectCouponInfoList(Long id, Long categoryId, Long userId);
}
