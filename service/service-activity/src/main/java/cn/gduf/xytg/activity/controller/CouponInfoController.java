package cn.gduf.xytg.activity.controller;

import cn.gduf.xytg.activity.service.CouponInfoService;
import cn.gduf.xytg.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.gduf.xytg.model.activity.CouponInfo;
import cn.gduf.xytg.vo.activity.CouponRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 优惠卷控制器
 * @date 2025/10/23 20:46
 */
@Api(tags = "优惠券接口")
@RestController
@RequestMapping("/admin/activity/couponInfo")
//@CrossOrigin
public class CouponInfoController {
    @Autowired
    private CouponInfoService couponInfoService;

    /**
     * 获取优惠券分页列表
     *
     * @param page  页码
     * @param limit 每页记录数
     * @return 优惠券分页列表
     */
    @ApiOperation("获取优惠券分页列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit) {
        IPage<CouponInfo> pageModel = couponInfoService.selectPageCouponInfo(page, limit);

        return Result.ok(pageModel);
    }

    /**
     * 添加优惠券
     *
     * @param couponInfo 优惠券信息
     * @return 添加结果
     */
    @ApiOperation("添加优惠券")
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo) {
        boolean save = couponInfoService.save(couponInfo);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改优惠券
     *
     * @param couponInfo 优惠券信息
     * @return 修改结果
     */
    @ApiOperation("修改优惠券")
    @PutMapping("update")
    public Result updateById(@RequestBody CouponInfo couponInfo) {
        boolean update = couponInfoService.updateById(couponInfo);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 删除优惠券
     *
     * @param id 优惠券id
     * @return 删除结果
     */
    @ApiOperation("删除优惠券")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = couponInfoService.removeById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 批量删除优惠券
     *
     * @param idList 优惠券id列表
     * @return 删除结果
     */
    @ApiOperation("批量删除优惠券")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean remove = couponInfoService.removeByIds(idList);

        return remove ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 根据id查询优惠券
     *
     * @param id 优惠券id
     * @return 优惠券信息
     */
    @ApiOperation("根据id查询优惠券")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CouponInfo couponInfo = couponInfoService.getCouponInfo(id);
        return Result.ok(couponInfo);
    }

    /**
     * 根据优惠卷id查询规则数据
     *
     * @param id 优惠券id
     * @return 规则数据
     */
    @ApiOperation("根据优惠卷id查询规则数据")
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id) {
        Map<String, Object> map =
                couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }

    /**
     * 添加优惠卷规则数据
     *
     * @param couponRuleVo 规则数据
     * @return 添加结果
     */
    @ApiOperation("添加优惠卷规则数据")
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo) {
        boolean save = couponInfoService.saveCouponRule(couponRuleVo);
        return save ? Result.ok(null) : Result.fail(null);
    }
}
