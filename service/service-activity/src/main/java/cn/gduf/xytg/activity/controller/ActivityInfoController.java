package cn.gduf.xytg.activity.controller;

import cn.gduf.xytg.activity.service.ActivityInfoService;
import cn.gduf.xytg.common.result.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gduf.xytg.model.activity.ActivityInfo;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.activity.ActivityRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 活动控制器
 * @date 2025/10/23 20:36
 */
@Api(tags = "活动控制器")
@RestController
@RequestMapping("/admin/activity/activityInfo")
//@CrossOrigin
public class ActivityInfoController {
    @Autowired
    private ActivityInfoService activityInfoService;

    /**
     * 活动列表
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation("活动列表")
    @GetMapping("{page}/{limit}")
    public Result listActivityInfo(@PathVariable Long page,
                                   @PathVariable Long limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        Page<ActivityInfo> pageModel = activityInfoService.selectPageActivityInfo(pageParam);

        return Result.ok(pageModel);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取活动")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ActivityInfo activityInfo = activityInfoService.getById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return Result.ok(activityInfo);
    }

    /**
     * 添加活动
     *
     * @param activityInfo
     * @return
     */
    @ApiOperation("添加活动")
    @PostMapping("save")
    public Result saveActivityInfo(@RequestBody ActivityInfo activityInfo) {
        boolean save = activityInfoService.save(activityInfo);
        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改活动
     *
     * @param activityInfo
     * @return
     */
    @ApiOperation("修改活动")
    @PutMapping("update")
    public Result updateActivityInfo(@RequestBody ActivityInfo activityInfo) {
        boolean update = activityInfoService.updateById(activityInfo);
        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除活动
     *
     * @param id
     * @return
     */
    @ApiOperation("删除活动")
    @DeleteMapping("remove/{id}")
    public Result removeActivityInfo(@PathVariable Long id) {
        boolean remove = activityInfoService.removeById(id);
        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除活动
     *
     * @param idList
     * @return
     */
    @ApiOperation("批量删除活动")
    @DeleteMapping("batchRemove")
    public Result batchRemoveActivityInfo(@RequestBody List<Long> idList) {
        boolean remove = activityInfoService.removeByIds(idList);
        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 查询活动规则
     *
     * @param id
     * @return
     */
    @ApiOperation("查询活动规则")
    @GetMapping("findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable Long id) {
        Map<String, Object> activityInfoList = activityInfoService.findActivityRuleList(id);
        return Result.ok(activityInfoList);
    }

    /**
     * 添加活动规则
     *
     * @param activityRuleVo
     * @return
     */
    @ApiOperation("添加活动规则")
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
        boolean save = activityInfoService.saveActivityRule(activityRuleVo);
        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 根据关键字查询商品
     *
     * @param keyword
     * @return
     */
    @ApiOperation("根据关键字查询商品")
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable String keyword) {
        List<SkuInfo> list = activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(list);
    }
}
