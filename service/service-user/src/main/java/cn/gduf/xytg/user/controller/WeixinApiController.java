package cn.gduf.xytg.user.controller;

import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.common.constant.RedisConst;
import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.common.utils.JwtHelper;
import cn.gduf.xytg.user.service.UserService;
import cn.gduf.xytg.user.utils.ConstantPropertiesUtil;
import cn.gduf.xytg.user.utils.HttpClientUtils;
import com.alibaba.fastjson.JSONObject;
import cn.gduf.xytg.enums.UserType;
import cn.gduf.xytg.enums.user.User;
import cn.gduf.xytg.vo.user.LeaderAddressVo;
import cn.gduf.xytg.vo.user.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 微信接口控制器
 * @date 2025/10/23 23:33
 */
@Api(tags = "微信接口控制器")
@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 微信登录获取授权
     *
     * @param code 微信登录成功返回的 code
     * @return
     */
    @ApiOperation(value = "微信登录获取授权")
    @GetMapping("/wxLogin/{code}")
    public Result wxLogin(@PathVariable String code) {
        // 获取微信发送的code
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;

        // code + 小程序id + 小程序密钥
        // 请求微信接口服务
        // 详细请见 https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
        StringBuffer url = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");

        String tokenUrl = String.format(url.toString(),
                wxOpenAppId,
                wxOpenAppId,
                code);

        // 利用 HttpClient 发送请求
        String result = null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new XytgException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        // 请求微信接口服务，返回 session_key, openid
        JSONObject jsonObject = JSONObject.parseObject(result);
        String sessionKey = jsonObject.getString("session_key");
        String openId = jsonObject.getString("openid");

        // 通过 openId 判断是否是第一次使用微信授权登录
        User user = userService.getUserByOpenId(openId);
        if (user == null) {
            // 添加微信用户信息到数据库中
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }

        // 根据 userId 查询提货点和团长信息
        LeaderAddressVo leaderAddressVo =
                userService.getLeaderAddressByUserId(user.getId());

        // 使用 JWT 工具根据 userId 和 userName 生成 token 字符串
        String token =
                JwtHelper.createToken(user.getId(), user.getNickName());

        // 获取当前登陆用户信息, 放到 Redis 中并设置有效时间
        UserLoginVo userLoginVo =
                userService.getUserLoginVo(user, leaderAddressVo);
        redisTemplate.opsForValue()
                .set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(), // key
                        userLoginVo, // value
                        RedisConst.USERKEY_TIMEOUT, // 过期时间
                        TimeUnit.DAYS); // 时间单位

        // 将数据封装为 map 返回给前端
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        map.put("leaderAddressVo", leaderAddressVo);

        return Result.ok(map);
    }

    /**
     * 更新用户昵称与头像
     * @param user 包含新昵称和头像URL的用户信息对象
     * @return Result 操作结果，成功返回Result.ok(null)，失败返回Result.fail(null)
     */
    @ApiOperation("更新用户昵称与头像")
    @PostMapping("/auth/updateUser")
    public Result updateUser(@RequestBody User user) {
        // 获取当前登录用户的信息
        User userInfo = userService.getById(AuthContextHolder.getUserId());

        // 设置新的昵称和头像URL，昵称中的特殊字符被替换为*
        userInfo.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        userInfo.setPhotoUrl(user.getPhotoUrl());

        // 更新用户信息并返回结果
        boolean update = userService.updateById(userInfo);
        return update ? Result.ok(null) : Result.fail(null);
    }
}
