package cn.gduf.xytg.product.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.product.service.AttrService;
import cn.gduf.xytg.model.product.Attr;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品属性控制器
 * @date 2025/10/22 21:25
 */
@Api(tags = "商品属性控制器")
@RestController
@RequestMapping("/admin/product/attr")
//@CrossOrigin
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 根据平台属性分组id查询
     *
     * @param groupId
     * @return
     */
    @ApiOperation("根据平台属性分组id查询")
    @GetMapping("{groupId}")
    public Result getAttrByGroupId(@PathVariable Long groupId) {
        List<Attr> list = attrService.getAttrListByGroupId(groupId);
        return Result.ok(list);
    }

    /**
     * 获取商品属性
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取商品属性")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Attr attr = attrService.getById(id);
        return Result.ok(attr);
    }

    /**
     * 新增商品属性
     *
     * @param attr
     * @return
     */
    @ApiOperation(value = "新增商品属性")
    @PostMapping("save")
    public Result save(@RequestBody Attr attr) {
        boolean save = attrService.save(attr);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改商品属性
     *
     * @param attr
     * @return
     */
    @ApiOperation(value = "修改商品属性")
    @PutMapping("update")
    public Result updateById(@RequestBody Attr attr) {
        boolean update = attrService.updateById(attr);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除商品属性
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除商品属性")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = attrService.removeById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 根据id列表删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean removeByIds = attrService.removeByIds(idList);

        return removeByIds ? Result.ok(null) : Result.fail(null);
    }
}
