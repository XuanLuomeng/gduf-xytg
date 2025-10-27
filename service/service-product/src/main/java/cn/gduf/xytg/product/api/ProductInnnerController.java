package cn.gduf.xytg.product.api;

import cn.gduf.xytg.product.service.CategoryService;
import cn.gduf.xytg.product.service.SkuInfoService;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.product.SkuInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品信息管理模块内部接口
 * @date 2025/10/23 19:28
 */
@RestController
@RequestMapping("/api/product")
public class ProductInnnerController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 获取商品分类
     *
     * @param categoryId
     * @return
     */
    @ApiOperation("获取商品分类")
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);

        return category;
    }

    /**
     * 获取商品
     *
     * @param skuId
     * @return
     */
    @ApiOperation("获取商品")
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        SkuInfo skuInfo = skuInfoService.getById(skuId);

        return skuInfo;
    }

    /**
     * 批量获取商品
     *
     * @param skuIdList
     * @return
     */
    @ApiOperation("批量获取商品")
    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList) {
        List<SkuInfo> list = skuInfoService.findSkuInfoList(skuIdList);
        return list;
    }

    /**
     * 根据关键字查询商品
     *
     * @param keyword
     * @return
     */
    @ApiOperation("根据关键字查询商品")
    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> getSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        return skuInfoService.getSkuInfoByKeyword(keyword);
    }

    /**
     * 根据分类id列表查询分类
     *
     * @param randIdList
     * @return
     */
    @ApiOperation("根据分类id列表查询分类")
    @PostMapping("inner/findCategoryList")
    List<Category> findCategoryList(@RequestBody List<Long> randIdList) {
        List<Category> categoryList = categoryService.listByIds(randIdList);

        return categoryList;
    }

    /**
     * 查询所有分类
     *
     * @return
     */
    @ApiOperation("查询所有分类")
    @GetMapping("inner/findAllCategoryList")
    public List<Category> findAllCategoryList() {
        List<Category> list = categoryService.list();
        return list;
    }

    /**
     * 获取新人专享商品
     *
     * @return
     */
    @ApiOperation("获取新人专享商品")
    @GetMapping("inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList() {
        List<SkuInfo> list = skuInfoService.findNewPersonSkuInfoList();
        return list;
    }

    @ApiOperation("根据skuId获取sku信息")
    @GetMapping("inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId) {
        return skuInfoService.getSkuInfo(skuId);
    }
}
