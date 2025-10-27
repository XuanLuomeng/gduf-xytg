package cn.gduf.xytg.search.repository;

import cn.gduf.xytg.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品仓库接口
 * @date 2025/10/23 19:24
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {
    /**
     * 根据热度排序
     *
     * @param pageable 分页参数
     * @return Page<SkuEs>
     */
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);

    /**
     * 根据分类id和仓库id查询商品(Spring Data会自动实现)
     *
     * @param categoryId 分类id
     * @param wareId     仓库id
     * @param pageable   分页参数
     * @return Page<SkuEs>
     */
    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    /**
     * 根据关键字和仓库id查询商品(Spring Data会自动实现)
     *
     * @param keyword 关键字
     * @param wareId  仓库id
     * @param pageable 分页参数
     * @return Page<SkuEs>
     */
    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable pageable);
}
