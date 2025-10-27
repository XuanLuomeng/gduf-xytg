package cn.gduf.xytg.client.product;

import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.product.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 产品模块FeignClient
 * @date 2025/10/23 19:36
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {
    /**
     * 根据分类id获取分类
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId列表获取sku信息
     *
     * @param skuIdList
     * @return
     */
    @GetMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList);

    /**
     * 根据关键字获取sku信息
     *
     * @param keyword
     * @return
     */
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> getSkuInfoByKeyword(@PathVariable("keyword") String keyword);

    /**
     * 根据分类id列表获取分类列表
     *
     * @param randIdList
     * @return
     */
    @PostMapping("/api/product/inner/findCategoryList")
    List<Category> findCategoryList(@RequestBody List<Long> randIdList);

    /**
     * 获取所有分类列表
     *
     * @return
     */
    @GetMapping("/api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();

    /**
     * 获取新人专享商品
     *
     * @return
     */
    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId);
}
