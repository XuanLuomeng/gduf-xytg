package cn.gduf.xytg.service.impl;

import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.client.search.SkuFeignClient;
import cn.gduf.xytg.client.user.UserFeignClient;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.model.search.SkuEs;
import cn.gduf.xytg.service.HomeService;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 首页服务层实现类
 * @date 2025/10/25 21:47
 */
@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SkuFeignClient skuFeignClient;

    /**
     * 获取首页数据
     *
     * @param userId 用户id
     * @return 首页数据Map，包含用户地址信息、所有分类列表、新人专享商品列表、热门商品列表
     */
    @Override
    public Map<String, Object> homeDate(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取用户地址信息
        LeaderAddressVo leaderAddressVo =
                userFeignClient.getUserAddressByUserId(userId);
        result.put("leaderAddressVo", leaderAddressVo);

        // 获取所有商品分类列表
        List<Category> allCategoryList =
                productFeignClient.findAllCategoryList();
        result.put("allCategoryList", allCategoryList);

        // 获取新人专享商品列表
        List<SkuInfo> newPersonSkuInfoList =
                productFeignClient.findNewPersonSkuInfoList();
        result.put("newPersonSkuInfoList", newPersonSkuInfoList);

        // 获取热门商品列表
        List<SkuEs> hotSkuList =
                skuFeignClient.findHotSkuList();
        result.put("hotSkuList", hotSkuList);

        return result;
    }
}
