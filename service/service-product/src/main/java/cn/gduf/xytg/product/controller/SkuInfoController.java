package cn.gduf.xytg.product.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.product.SkuInfoQueryVo;
import cn.gduf.xytg.vo.product.SkuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 库存信息控制器
 * @date 2025/10/22 21:34
 */
@Api(tags = "库存信息控制器")
@RestController
@RequestMapping("/admin/product/skuInfo")
//@CrossOrigin
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * sku列表
     *
     * @param page
     * @param limit
     * @param skuInfoQueryVo
     * @return
     */
    @ApiOperation("sku列表")
    @GetMapping("{page}/{limit}")
    public Result listSkuInfo(@PathVariable Long page,
                              @PathVariable Long limit,
                              SkuInfoQueryVo skuInfoQueryVo) {
        Page<SkuInfo> pageParam = new Page<>(page, limit);
        IPage<SkuInfo> pageModel = skuInfoService.selectPageSkuInfo(pageParam, skuInfoQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 添加商品sku信息
     *
     * @param skuInfoVo
     * @return
     */
    @ApiOperation("添加商品sku信息")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo) {
        boolean save = skuInfoService.saveSkuInfo(skuInfoVo);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 获取sku信息
     *
     * @param id
     * @return
     */
    @ApiOperation("获取sku信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfo(id);

        return Result.ok(skuInfoVo);
    }

    /**
     * 修改sku
     *
     * @param skuInfoVo
     * @return
     */
    @ApiOperation("修改sku")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo) {
        boolean update = skuInfoService.updateSkuInfo(skuInfoVo);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除sku
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除sku")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = skuInfoService.removeById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "根据id列表删除sku")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean removeByIds = skuInfoService.removeByIds(idList);
        return removeByIds ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 商品审核
     *
     * @param skuId
     * @param status
     * @return
     */
    @ApiOperation("商品审核")
    @GetMapping("check/{skuId}/{status}")
    public Result check(@PathVariable Long skuId,
                        @PathVariable Integer status) {
        boolean check = skuInfoService.check(skuId, status);

        return check ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 商品上下架
     *
     * @param skuId
     * @param status
     * @return
     */
    @ApiOperation("商品上下架")
    @GetMapping("publish/{skuId}/{status}")
    public Result publish(@PathVariable Long skuId,
                          @PathVariable Integer status) {
        boolean publish = skuInfoService.publish(skuId, status);

        return publish ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 新人专享
     *
     * @param skuId
     * @param status
     * @return
     */
    @ApiOperation("新人专享")
    @GetMapping("isNewPerson/{skuId}/{status}")
    public Result isNewPerson(@PathVariable Long skuId,
                              @PathVariable Integer status) {
        boolean isNewPerson = skuInfoService.isNewPerson(skuId, status);

        return isNewPerson ? Result.ok(null) : Result.fail(null);
    }
}
