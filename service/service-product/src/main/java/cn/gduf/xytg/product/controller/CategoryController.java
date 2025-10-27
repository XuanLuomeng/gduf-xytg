package cn.gduf.xytg.product.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.product.service.CategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.vo.product.CategoryQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品分类控制器
 * @date 2025/10/22 21:33
 */
@Api(tags = "商品分类接口")
@RestController
@RequestMapping("/admin/product/category")
//@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取商品分类列表
     *
     * @param page
     * @param list
     * @param categoryQueryVo
     * @return
     */
    @ApiOperation("获取商品分类列表")
    @GetMapping("{page}/{list}")
    public Result listCategory(@PathVariable Long page,
                               @PathVariable Long list,
                               CategoryQueryVo categoryQueryVo) {
        // 创建page对象
        Page<Category> pageParam = new Page<>(page, list);
        // 调用service方法
        IPage<Category> pageModel = categoryService.selectPageCategory(pageParam, categoryQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 获取商品分类列表
     *
     * @param id
     * @return
     */
    @ApiOperation("获取商品分类列表")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Category category = categoryService.getById(id);

        return Result.ok(category);
    }

    /**
     * 添加商品分类
     *
     * @param category
     * @return
     */
    @ApiOperation("添加商品分类")
    @PostMapping("save")
    public Result saveCategory(@RequestBody Category category) {
        boolean save = categoryService.save(category);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改商品分类
     *
     * @param category
     * @return
     */
    @ApiOperation("修改商品分类")
    @PutMapping("update")
    public Result updateCategory(@RequestBody Category category) {
        boolean update = categoryService.updateById(category);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除商品分类
     *
     * @param id
     * @return
     */
    @ApiOperation("删除商品分类")
    @DeleteMapping("remove/{id}")
    public Result removeCategory(@PathVariable Long id) {
        boolean remove = categoryService.removeById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除商品分类
     *
     * @param idList
     * @return
     */
    @ApiOperation("批量删除商品分类")
    @DeleteMapping("batchRemove")
    public Result batchRemoveCategory(@RequestBody List<Long> idList) {
        boolean remove = categoryService.removeByIds(idList);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 获取所有商品分类
     *
     * @return
     */
    @ApiOperation("获取所有商品分类")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Category> list = categoryService.list();
        return Result.ok(list);
    }
}
