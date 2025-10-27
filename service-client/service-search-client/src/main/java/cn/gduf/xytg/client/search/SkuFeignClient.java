package cn.gduf.xytg.client.search;

import cn.gduf.xytg.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品Es FeignClient
 * @date 2025/10/25 22:53
 */
@FeignClient("service-search")
public interface SkuFeignClient {
    /**
     * 获取爆款商品
     *
     * @return
     */
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList();

    /**
     * 获取商品信息
     *
     * @param skuId 商品ID
     * @return 商品信息
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}
