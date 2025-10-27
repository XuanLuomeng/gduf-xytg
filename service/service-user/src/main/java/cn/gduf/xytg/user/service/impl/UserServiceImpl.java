package cn.gduf.xytg.user.service.impl;

import cn.gduf.xytg.user.mapper.LeaderMapper;
import cn.gduf.xytg.user.mapper.UserDeliveryMapper;
import cn.gduf.xytg.user.mapper.UserMapper;
import cn.gduf.xytg.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.enums.user.Leader;
import cn.gduf.xytg.enums.user.User;
import cn.gduf.xytg.enums.user.UserDelivery;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import cn.gduf.xytg.vo.user.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户服务实现类
 * @date 2025/10/23 23:30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private LeaderMapper leaderMapper;

    /**
     * 根据openId查询用户
     *
     * @param openId 微信openId
     * @return 用户
     */
    @Override
    public User getUserByOpenId(String openId) {
        User user = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenId, openId)
        );
        return user;
    }

    /**
     * 根据用户id查询团长收货地址
     *
     * @param id 用户id
     * @return 团长收货地址
     */
    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long id) {
        // 根据用户id查询默认收货地址
        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>().eq(UserDelivery::getUserId, id)
                        .eq(UserDelivery::getIsDefault, 1)
        );

        if (userDelivery == null) {
            return null;
        }

        // 查询团长详细信息
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());

        // 组装团长地址信息
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(id);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());

        return leaderAddressVo;
    }

    /**
     * 获取用户登录信息 (登录流程中前一个过程已经获取到了 user 和 leader 相关信息，在这里只需要装填信息即可)
     *
     * @param user            用户
     * @param leaderAddressVo 团长收货地址
     * @return 用户登录信息
     */
    @Override
    public UserLoginVo getUserLoginVo(User user, LeaderAddressVo leaderAddressVo) {
        // 创建用户登录信息对象并设置基本信息
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUserId(user.getId());
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setOpenId(user.getOpenId());
        userLoginVo.setIsNew(user.getIsNew());

        // 根据团长地址信息设置团长ID和仓库ID，如果为空则使用默认值
        if (leaderAddressVo != null) {
            userLoginVo.setLeaderId(leaderAddressVo.getLeaderId());
            userLoginVo.setWareId(leaderAddressVo.getWareId());
        } else {
            userLoginVo.setLeaderId(1L);
            userLoginVo.setWareId(1L);
        }

        return userLoginVo;
    }
}
