package cn.gduf.xytg.search.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.model.search.SkuEs;
import cn.gduf.xytg.search.service.SkuService;
import cn.gduf.xytg.vo.search.SkuEsQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品搜索控制器
 * @date 2025/10/23 19:22
 */
@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {
    @Autowired
    private SkuService skuService;

    /**
     * 上架商品
     *
     * @param skuId 商品ID
     * @return 操作结果，成功返回空数据的Result对象
     */
    @RequestMapping("inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    /**
     * 下架商品
     *
     * @param skuId 商品id
     * @return 操作结果，成功返回空数据的Result对象
     */
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }

    /**
     * 获取爆款商品列表
     *
     * @return 爆款商品列表
     */
    @ApiOperation("获取爆款商品")
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.finHotSkuList();
    }

    /**
     * 获取商品列表
     *
     * @param page         页码
     * @param limit        每页记录数
     * @param skuEsQueryVo 查询条件
     * @return 商品列表
     */
    @GetMapping("{page}/{limit}")
    public Result listSku(@PathVariable Integer page,
                          @PathVariable Integer limit,
                          SkuEsQueryVo skuEsQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SkuEs> pageModel = skuService.search(pageable, skuEsQueryVo);
        return Result.ok(pageModel);
    }

    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        boolean flag = skuService.incrHotScore(skuId);
        return flag;
    }
}

