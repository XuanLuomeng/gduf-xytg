package cn.gduf.xytg.product.service;

import cn.gduf.xytg.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.vo.product.SkuInfoQueryVo;
import cn.gduf.xytg.vo.product.SkuInfoVo;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品信息服务接口
 * @date 2025/10/22 21:19
 */
public interface SkuInfoService extends IService<SkuInfo> {
    /**
     * 分页查询商品信息
     *
     * @param pageParam
     * @param skuInfoQueryVo
     * @return
     */
    IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo);

    /**
     * 添加商品信息
     *
     * @param skuInfoVo
     */
    boolean saveSkuInfo(SkuInfoVo skuInfoVo);

    /**
     * 根据id查询商品信息
     *
     * @param id
     * @return
     */
    SkuInfoVo getSkuInfo(Long id);

    /**
     * 修改商品信息
     *
     * @param skuInfoVo
     * @return
     */
    boolean updateSkuInfo(SkuInfoVo skuInfoVo);

    /**
     * 商品审核
     *
     * @param skuId
     * @param status
     * @return
     */
    boolean check(Long skuId, Integer status);

    /**
     * 商品发布
     *
     * @param skuId
     * @param status
     * @return
     */
    boolean publish(Long skuId, Integer status);

    /**
     * 判断是否新人专享
     *
     * @param skuId
     * @param status
     * @return
     */
    boolean isNewPerson(Long skuId, Integer status);

    /**
     * 根据skuId列表得到sku信息列表
     *
     * @param skuIdList
     * @return
     */
    List<SkuInfo> findSkuInfoList(List<Long> skuIdList);

    /**
     * 根据关键字查询商品信息
     *
     * @param keyword
     * @return
     */
    List<SkuInfo> getSkuInfoByKeyword(String keyword);

    /**
     * 获取新人专享商品列表
     *
     * @return
     */
    List<SkuInfo> findNewPersonSkuInfoList();

    /**
     * 锁定库存
     *
     * @param skuStockLockVoList
     * @param orderNo
     * @return
     */
    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);

    /**
     * 减库存
     *
     * @param orderNo
     */
    void minusStock(String orderNo);

    /**
     * 恢复库存
     *
     * @param orderNo
     */
    void rollbackStock(String orderNo);
}