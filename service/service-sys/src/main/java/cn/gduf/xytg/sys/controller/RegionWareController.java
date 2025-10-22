package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.sys.service.RegionWareService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gduf.xytg.model.sys.RegionWare;
import com.gduf.xytg.vo.sys.RegionWareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 城市仓库关联控制器
 * @date 2025/10/21 22:51
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
//@CrossOrigin
public class RegionWareController {
    @Autowired
    private RegionWareService regionWareService;

    @ApiOperation("开通区域列表")
    @GetMapping("{page}/{limit}")
    public Result listRegionWare(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> pageParam = new Page<>(page, limit);
        IPage<RegionWare> pageModel = regionWareService.selectPageRegionWare(pageParam, regionWareQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("添加开通区域")
    @PostMapping("save")
    public Result saveRegionWare(@RequestBody RegionWare regionWare) {
        return regionWareService.saveRegionWare(regionWare)
                ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("删除开通区域")
    @DeleteMapping("remove/{id}")
    public Result removeRegionWare(@PathVariable Long id) {
        return regionWareService.removeById(id)
                ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("修改开通区域状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status){
        return regionWareService.updateStatus(id, status)
                ? Result.ok(null) : Result.fail(null);
    }
}
