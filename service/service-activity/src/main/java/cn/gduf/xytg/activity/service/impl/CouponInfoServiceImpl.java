package cn.gduf.xytg.activity.service.impl;

import cn.gduf.xytg.activity.mapper.CouponInfoMapper;
import cn.gduf.xytg.activity.mapper.CouponRangeMapper;
import cn.gduf.xytg.activity.service.CouponInfoService;
import cn.gduf.xytg.client.product.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.enums.CouponRangeType;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.model.activity.CouponRange;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 优惠卷信息服务实现类
 * @date 2025/10/23 20:37
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 分页查询优惠卷信息
     *
     * @param page  索引
     * @param limit 每页记录数
     * @return 优惠卷信息分页数据
     */
    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {
        // 创建分页对象
        IPage<CouponInfo> pageModel = new Page<>(page, limit);

        // 执行分页查询
        pageModel = baseMapper.selectPage(pageModel, null);

        // 处理优惠券类型和范围类型的显示文本
        pageModel.getRecords().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if (rangeType != null) {
                item.setRangeTypeString(rangeType.getComment());
            }
        });

        return pageModel;
    }

    /**
     * 根据id查询优惠卷信息
     *
     * @param id 优惠卷id
     * @return 优惠卷信息
     */
    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);

        // 处理优惠券类型和范围类型的显示文本
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }

        return couponInfo;
    }

    /**
     * 根据优惠卷id查询优惠卷规则
     *
     * @param id 优惠卷id
     * @return 优惠卷规则
     */
    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        // 查询优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(id);

        // 查询优惠券使用范围列表
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id)
        );

        // 提取范围ID列表
        List<Long> randIdList =
                couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();

        // 根据优惠券范围类型查询对应的商品或分类信息
        if (!CollectionUtils.isEmpty(randIdList)) {
            if (couponInfo.getRangeType() == CouponRangeType.SKU) {
                List<SkuInfo> skuInfoList =
                        productFeignClient.findSkuInfoList(randIdList);

                result.put("skuInfoList", skuInfoList);
            } else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                List<Category> categoryList =
                        productFeignClient.findCategoryList(randIdList);

                result.put("categoryList", categoryList);
            }
        }

        return result;
    }

    /**
     * 保存优惠卷规则
     *
     * @param couponRuleVo 优惠卷规则对象，包含优惠券ID、使用范围类型、满足条件金额、优惠金额、范围描述和优惠券范围列表
     * @return 保存结果，删除成功且更新成功时返回true，否则返回false
     */
    @Override
    @Transactional
    public boolean saveCouponRule(CouponRuleVo couponRuleVo) {
        // 删除原有的优惠券使用范围数据
        int deleteRange = couponRangeMapper.delete(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId())
        );

        // 更新优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        int updateCouponInfo = baseMapper.updateById(couponInfo);

        // 保存新的优惠券使用范围数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();

        couponRangeList.stream().forEach(item -> {
            item.setCouponId(couponInfo.getId());
            couponRangeMapper.insert(item);
        });

        return deleteRange > 0 && updateCouponInfo > 0;
    }

    /**
     * 查询优惠券列表
     *
     * @param skuId  商品ID
     * @param userId 用户ID
     * @return 优惠券列表
     */
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        List<CouponInfo> couponInfoList =baseMapper.selectCouponInfoList(skuInfo.getId(),
                skuInfo.getCategoryId(),
                userId);

        return couponInfoList;
    }
}
