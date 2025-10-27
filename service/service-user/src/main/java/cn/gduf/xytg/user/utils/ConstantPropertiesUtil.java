package cn.gduf.xytg.user.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 微信配置类
 * @date 2025/10/25 15:30
 */
@Component
public class ConstantPropertiesUtil implements InitializingBean {
    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;

    /**
     * 在属性设置完成后执行初始化操作，将实例变量的值赋给静态变量
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 将从配置文件中读取的微信应用ID和密钥赋值给静态变量，便于全局访问
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
    }
}

