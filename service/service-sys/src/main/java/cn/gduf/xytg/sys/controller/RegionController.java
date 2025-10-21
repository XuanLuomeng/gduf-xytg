package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.sys.service.RegionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
