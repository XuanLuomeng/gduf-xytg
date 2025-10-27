package cn.gduf.xytg.user.api;

import cn.gduf.xytg.user.service.UserService;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 团长地址接口控制器
 * @date 2025/10/25 21:56
 */
@Api(tags = "团长地址接口控制器")
@RestController
@RequestMapping("/api/user/leader")
public class LeaderAddressApiController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "服务器远程调用获取团长地址")
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId) {
        LeaderAddressVo leaderAddress = userService.getLeaderAddressByUserId(userId);
        return leaderAddress;
    }
}
