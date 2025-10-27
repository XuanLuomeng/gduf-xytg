package cn.gduf.xytg.service;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 首页服务接口
 * @date 2025/10/25 21:45
 */
public interface HomeService {
    /**
     * 获取首页数据
     *
     * @param userId 用户ID
     * @return Map<String, Object> 包含首页数据的Map对象
     */
    Map<String, Object> homeDate(Long userId);
}
