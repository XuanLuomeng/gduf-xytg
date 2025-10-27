package cn.gduf.xytg.product.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.product.service.AttrGroupService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gduf.xytg.model.product.AttrGroup;
import cn.gduf.xytg.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性分组控制器
 * @date 2025/10/22 21:32
 */
@Api(tags = "属性分组控制器")
@RestController
@RequestMapping("/admin/product/attrGroup")
//@CrossOrigin
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 平台属性分组列表
     *
     * @param page
     * @param limit
     * @param attrGroupQueryVo
     * @return
     */
    @ApiOperation("平台属性分组列表")
    @GetMapping("{page}/{limit}")
    public Result listAttrGroup(@PathVariable Long page,
                                @PathVariable Long limit,
                                AttrGroupQueryVo attrGroupQueryVo) {
        // 创建page对象
        Page<AttrGroup> pageParam = new Page<>(page, limit);
        // 调用service方法
        IPage<AttrGroup> pageModel = attrGroupService.selectPageAttrGroup(pageParam, attrGroupQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 查询所有平台属性分组列表
     *
     * @return
     */
    @ApiOperation("查询所有平台属性分组列表")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<AttrGroup> list = attrGroupService.findAllListAttrGroup();

        return Result.ok(list);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取平台属性分组")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        AttrGroup attrGroup = attrGroupService.getById(id);

        return Result.ok(attrGroup);
    }

    /**
     * 新增平台属性分组
     *
     * @param attrGroup
     * @return
     */
    @ApiOperation(value = "新增平台属性分组")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup) {
        boolean save = attrGroupService.save(attrGroup);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改平台属性分组
     *
     * @param attrGroup
     * @return
     */
    @ApiOperation(value = "修改平台属性分组")
    @PutMapping("update")
    public Result updateById(@RequestBody AttrGroup attrGroup) {
        boolean update = attrGroupService.updateById(attrGroup);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除平台属性分组
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除平台属性分组")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = attrGroupService.removeById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean removeByIds = attrGroupService.removeByIds(idList);

        return removeByIds ? Result.ok(null) : Result.fail(null);
    }
}
