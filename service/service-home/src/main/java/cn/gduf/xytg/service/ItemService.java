package cn.gduf.xytg.service;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品详情服务接口
 * @date 2025/10/27 20:31
 */
public interface ItemService {
    /**
     * 获取商品详情
     *
     * @param id     商品id
     * @param userId 用户id
     * @return 商品详情
     */
    Map<String, Object> item(Long id, Long userId);
}
