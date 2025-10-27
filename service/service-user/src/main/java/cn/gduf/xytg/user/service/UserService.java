package cn.gduf.xytg.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.enums.user.User;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import cn.gduf.xytg.vo.user.UserLoginVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户服务接口
 * @date 2025/10/23 23:30
 */
public interface UserService extends IService<User> {
    /**
     * 根据openId查询用户
     *
     * @param openId
     * @return
     */
    User getUserByOpenId(String openId);

    /**
     * 根据用户id查询团长收货地址
     *
     * @param id
     * @return
     */
    LeaderAddressVo getLeaderAddressByUserId(Long id);

    /**
     * 获取用户登录信息
     *
     * @param user
     * @param leaderAddressVo
     * @return
     */
    UserLoginVo getUserLoginVo(User user, LeaderAddressVo leaderAddressVo);
}
