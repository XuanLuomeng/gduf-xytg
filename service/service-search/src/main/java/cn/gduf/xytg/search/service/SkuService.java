package cn.gduf.xytg.search.service;

import cn.gduf.xytg.model.search.SkuEs;
import cn.gduf.xytg.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品服务接口
 * @date 2025/10/23 19:22
 */
public interface SkuService {
    /**
     * 上架商品
     *
     * @param skuId 商品id
     */
    void upperSku(Long skuId);

    /**
     * 下架商品
     *
     * @param skuId 商品id
     */
    void lowerSku(Long skuId);

    /**
     * 获取最热门商品
     *
     * @return 最热门商品列表
     */
    List<SkuEs> finHotSkuList();

    /**
     * 商品搜索
     *
     * @param pageable 分页参数
     * @param skuEsQueryVo 搜索条件
     * @return 搜索结果
     */
    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    /**
     * 商品热度排名
     *
     * @param skuId 商品id
     * @return 是否成功
     */
    boolean incrHotScore(Long skuId);
}
