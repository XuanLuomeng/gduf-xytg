package cn.gduf.xytg.service.impl;

import cn.gduf.xytg.activity.client.ActivityFeignClient;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.client.search.SkuFeignClient;
import cn.gduf.xytg.service.ItemService;
import cn.gduf.xytg.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品详情服务实现类
 * @date 2025/10/27 20:31
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private SkuFeignClient skuFeignClient;


    /**
     * 获取商品详情
     *
     * @param id     商品id
     * @param userId 用户id
     * @return 商品详情
     */
    @Override
    public Map<String, Object> item(Long id, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 异步获取商品SKU信息
        CompletableFuture<SkuInfoVo> skuInfo = CompletableFuture.supplyAsync(() -> {
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(id);
            result.put("skuInfo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        // 异步获取活动信息
        CompletableFuture<Void> activity = CompletableFuture.runAsync(() -> {
            Map<String, Object> activityMap =
                    activityFeignClient.findActivityAndCoupon(id, userId);
            result.put("activity", activityMap);
        }, threadPoolExecutor);

        // 异步更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            skuFeignClient.incrHotScore(id);
        }, threadPoolExecutor);

        // 等待所有异步任务完成
        CompletableFuture.allOf(
                skuInfo,
                activity,
                hotCompletableFuture
        ).join();

        return result;
    }
}
