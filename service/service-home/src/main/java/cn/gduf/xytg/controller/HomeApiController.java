package cn.gduf.xytg.controller;

import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.service.HomeService;
import cn.gduf.xytg.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 首页接口控制器
 * @date 2025/10/25 21:46
 */
@Api(tags = "首页接口控制器")
@RestController
@RequestMapping("/api/home")
public class HomeApiController {
    @Autowired
    private HomeService homeService;

    /**
     * 首页数据显示接口
     *
     * @return
     */
    @ApiOperation("首页数据显示接口")
    @GetMapping("index")
    public Result index(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = homeService.homeDate(userId);
        return Result.ok(map);
    }
}
