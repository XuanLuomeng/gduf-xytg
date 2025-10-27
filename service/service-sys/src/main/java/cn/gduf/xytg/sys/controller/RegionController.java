package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.sys.service.RegionService;
import cn.gduf.xytg.model.sys.Region;
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
 * @description 地区控制器
 * @date 2025/10/21 22:50
 */
@Api(tags = "地区管理接口")
@RestController
@RequestMapping("/admin/sys/region")
//@CrossOrigin
public class RegionController {
    @Autowired
    private RegionService regionService;

    @ApiOperation("根据区域关键字查询区域列表信息")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Object findRegionByKeyword(@PathVariable String keyword) {
        List<Region> list = regionService.getRegionByKeyword(keyword);

        return Result.ok(list);
    }

    @ApiOperation("通过父节点ID查找")
    @GetMapping("findByParentId/{parentId}")
    public Object findByParentId(@PathVariable String parentId) {
        List<Region> list = regionService.getRegionByParentId(parentId);

        return Result.ok(list);
    }
}
