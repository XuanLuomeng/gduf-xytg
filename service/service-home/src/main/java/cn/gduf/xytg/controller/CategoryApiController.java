package cn.gduf.xytg.controller;

import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.model.product.Category;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品分类Api控制器
 * @date 2025/10/26 20:58
 */
@Api(tags = "商品分类")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {
    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 查询所有商品分类
     *
     * @return
     */
    @GetMapping("category")
    public Result categoryList() {
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        return Result.ok(categoryList);
    }
}
