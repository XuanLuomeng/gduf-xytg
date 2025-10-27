package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.sys.service.WareService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gduf.xytg.model.sys.Ware;
import cn.gduf.xytg.vo.product.WareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 仓库控制类
 * @date 2025/10/21 22:52
 */
@Api(tags = "仓库管理接口")
@RestController
@RequestMapping("/admin/sys/ware")
//@CrossOrigin
public class WareController {
    @Autowired
    private WareService wareService;

    @ApiOperation("查询所有仓库")
    @GetMapping("findAllList")
    public Object findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }

    @ApiOperation("分页查询仓库")
    @GetMapping("{page}/{limit}")
    public Result pageList(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           WareQueryVo wareQueryVo) {
        Page<Ware> pageParam = new Page<>(page, limit);
        IPage<Ware> pageModel = wareService.selectPageRegion(pageParam, wareQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("添加仓库")
    @GetMapping("save")
    public Result save(Ware ware) {
        return wareService.save(ware) ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("修改仓库")
    @GetMapping("update")
    public Result update(Ware ware) {
        return wareService.updateById(ware) ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("删除仓库")
    @GetMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        return wareService.removeById(id) ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("批量删除仓库")
    @GetMapping("batchRemove")
    public Result batchRemove(List<Long> idList) {
        return wareService.removeByIds(idList) ? Result.ok(null) : Result.fail(null);
    }
}
