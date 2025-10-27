package cn.gduf.xytg.client.user;

import cn.gduf.xytg.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户FeignClient
 * @date 2025/10/25 22:09
 */
@FeignClient(value = "service-user", path = "/api/user")
public interface UserFeignClient {
    /**
     * 根据用户ID获取用户地址信息
     *
     * @param userId 用户ID
     * @return LeaderAddressVo 用户地址信息
     */
    @GetMapping("/api/user/leader/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId);
}
