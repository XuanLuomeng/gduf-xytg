package cn.gduf.xytg.common.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description JWT 工具类
 * @date 2025/10/23 23:38
 */
public class JwtHelper {
    // token过期时间，单位毫秒，默认为365天
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    // token签名密钥
    private static String tokenSignKey = "xytg";

    /**
     * 创建JWT Token
     *
     * 进阶: 将登录密码也作为Token的一部分
     *
     * @param userId   用户ID，用于在token中存储用户标识
     * @param userName 用户名，用于在token中存储用户名信息
     * @return 生成的JWT Token字符串
     */
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("xytg-USER")                                    // 设置主题
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))  // 设置过期时间
                .claim("userId", userId)                                   // 添加用户ID声明
                .claim("userName", userName)                               // 添加用户名声明
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)          // 使用HS512算法签名
                .compressWith(CompressionCodecs.GZIP)                      // 使用GZIP压缩
                .compact();                                                // 生成token字符串
        return token;
    }

    /**
     * 从JWT Token中解析用户ID
     *
     * @param token JWT Token字符串
     * @return 用户ID，如果token为空则返回null
     */
    public static Long getUserId(String token) {
        // 检查token是否为空
        if (StringUtils.isEmpty(token)) return null;

        // 解析token并获取声明部分
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        // 获取用户ID并转换为Long类型
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
        // return 1L;
    }

    /**
     * 从JWT Token中解析用户名
     *
     * @param token JWT Token字符串
     * @return 用户名，如果token为空则返回空字符串
     */
    public static String getUserName(String token) {
        // 检查token是否为空
        if (StringUtils.isEmpty(token)) return "";

        // 解析token并获取声明部分
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        // 返回用户名
        return (String) claims.get("userName");
    }

    /**
     * 移除JWT Token（逻辑删除）
     *
     * @param token 需要移除的JWT Token
     */
    public static void removeToken(String token) {
        // jwt token无需删除，客户端扔掉即可。
    }
}
