package com.gduf.xytg.acl.service;

import cn.gduf.xytg.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 登录接口
 * @date 2025/10/18 22:29
 */
@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin  // 允许跨域
public class IndexController {
    /**
     * 登录
     *
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("login")
    public Result login() {
        Map<String, String> map = new HashMap<>();
        map.put("token", "admin-token");

        return Result.ok(null);
    }

    /**
     * 获取信息
     *
     * @return
     */
    @ApiOperation("获取信息")
    @GetMapping("info")
    public Result info() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "admin");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        return Result.ok(map);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("退出")
    @PostMapping("logout")
    public Result logout() {
        return Result.ok(null);
    }
}
