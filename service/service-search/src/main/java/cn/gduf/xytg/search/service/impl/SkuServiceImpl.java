package cn.gduf.xytg.search.service.impl;

import cn.gduf.xytg.activity.client.ActivityFeignClient;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.common.auth.AuthContextHolder;
import cn.gduf.xytg.search.repository.SkuRepository;
import cn.gduf.xytg.search.service.SkuService;
import cn.gduf.xytg.enums.SkuType;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.model.search.SkuEs;
import cn.gduf.xytg.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品服务实现类
 * @date 2025/10/23 19:23
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 上架商品
     *
     * @param skuId
     */
    @Override
    public void upperSku(Long skuId) {
        // 通过远程调用，查询SKU信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());

        // 获取数据封装到SkuEs
        SkuEs skuEs = new SkuEs();
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }

        // 封装sku
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) {//普通商品
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }

        // 保存数据到ES
        skuRepository.save(skuEs);
    }

    /**
     * 删除数据
     *
     * @param skuId
     */
    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    /**
     * 获取最热门商品
     *
     * @return List<SkuEs> 热门商品列表，按热度评分降序排列，最多返回10条记录
     */
    @Override
    public List<SkuEs> finHotSkuList() {
        // 创建分页请求，获取第一页数据，每页10条记录
        Pageable pageable = PageRequest.of(0, 10);
        // 根据热度评分降序查询商品数据(findByOrderByHotScoreDesc为Spring Data的一种用法)
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        // 获取查询结果内容
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    /**
     * 商品搜索
     *
     * @param pageable     分页参数
     * @param skuEsQueryVo 查询条件
     * @return Page<SkuEs> 搜索结果
     */
    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        // 设置仓库ID
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        Page<SkuEs> pageModel = null;

        // 根据是否有关键词执行不同的查询逻辑
        String keyword = skuEsQueryVo.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            // 无关键词时按分类ID和仓库ID查询
            pageModel =
                    skuRepository.findByCategoryIdAndWareId(
                            skuEsQueryVo.getCategoryId(),
                            skuEsQueryVo.getWareId(),
                            pageable
                    );
        } else {
            // 有关键词时按关键词和仓库ID查询
            pageModel =
                    skuRepository.findByKeywordAndWareId(
                            skuEsQueryVo.getKeyword(),
                            skuEsQueryVo.getWareId(),
                            pageable
                    );
        }

        // 获取商品列表并设置活动规则
        List<SkuEs> skuEsList = pageModel.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)) {
            // 提取商品ID列表
            List<Long> skuIdList =
                    skuEsList.stream()
                            .map(item -> item.getId())
                            .collect(Collectors.toList());

            // 远程调用获取商品对应的活动规则
            Map<Long, List<String>> skuIdToRuleListMap =
                    activityFeignClient.findActivity(skuIdList);

            // 将活动规则设置到对应的商品对象中
            if (skuIdToRuleListMap != null) {
                skuEsList.forEach(skuEs -> {
                    skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
                });
            }
        }

        return pageModel;
    }

    /**
     * 增加商品热度
     *
     * @param skuId 商品ID
     * @return 是否成功
     */
    @Override
    public boolean incrHotScore(Long skuId) {
        String key = "hotScore";

        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);

        if (hotScore % 10 == 0) {
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuRepository.save(skuEs);
        }

        return true;
    }
}
