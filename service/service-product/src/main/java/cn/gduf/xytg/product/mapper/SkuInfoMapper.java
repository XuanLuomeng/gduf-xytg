package cn.gduf.xytg.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.gduf.xytg.model.product.SkuInfo;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 库存信息 Mapper
 * @date 2025/10/22 21:24
 */
@Repository
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 检查库存锁定状态
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 锁定库存
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 解锁库存
     *
     * @param skuId
     * @param skuNum
     */
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 减库存
     *
     * @param skuId
     * @param skuNum
     */
    void minusStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    /**
     * 恢复库存
     *
     * @param skuId
     * @param skuNum
     */
    void rollbackStock(Long skuId, Integer skuNum);
}
