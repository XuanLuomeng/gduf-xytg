package cn.gduf.xytg.sys.controller;

import cn.gduf.xytg.sys.service.RegionWareService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
