package cn.gduf.xytg.controller;

import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 详情Api 控制器
 * @date 2025/10/27 20:30
 */
@Api(tags = "商品详情")
@RestController
@RequestMapping("api/home")
public class ItemApiController {
    @Autowired
    private ItemService itemService;

    /**
     * 获取商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    @ApiOperation("获取商品详情")
    @GetMapping("item/{id}")
    public Result index(@PathVariable Long id) {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = itemService.item(id, userId);
        return Result.ok(map);
    }
}
