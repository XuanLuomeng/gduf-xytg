package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.sys.service.WareService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
