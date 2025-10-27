package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.common.constant.MqConst;
import cn.gduf.xytg.common.service.RabbitService;
import cn.gduf.xytg.product.mapper.SkuInfoMapper;
import cn.gduf.xytg.product.service.SkuAttrValueService;
import cn.gduf.xytg.product.service.SkuImageService;
import cn.gduf.xytg.product.service.SkuInfoService;
import cn.gduf.xytg.product.service.SkuPosterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.SkuAttrValue;
import cn.gduf.xytg.model.product.SkuImage;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.model.product.SkuPoster;
import cn.gduf.xytg.vo.product.SkuInfoQueryVo;
import cn.gduf.xytg.vo.product.SkuInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品信息服务实现类
 * @date 2025/10/22 21:31
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    // 商品图片服务
    @Autowired
    private SkuImageService skuImageService;

    // 商品属性服务
    @Autowired
    private SkuAttrValueService skuAttrValueService;

    // 商品海报服务
    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 分页查询商品信息
     *
     * @param pageParam
     * @param skuInfoQueryVo
     * @return
     */
    @Override
    public IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        // 获取查询参数
        String keyword = skuInfoQueryVo.getKeyword();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String skuType = skuInfoQueryVo.getSkuType();

        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();

        // 封装查询条件，防止输入空格
        if (!StringUtils.isEmpty(keyword)){
            wrapper.like(SkuInfo::getSkuName, keyword);
        }

        if (!StringUtils.isEmpty(categoryId)){
            wrapper.eq(SkuInfo::getCategoryId, categoryId);
        }

        if (!StringUtils.isEmpty(skuType)){
            wrapper.eq(SkuInfo::getSkuType, skuType);
        }

        return baseMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 保存商品信息
     *
     * @param skuInfoVo
     * @return
     */
    @Override
    @Transactional
    public boolean saveSkuInfo(SkuInfoVo skuInfoVo) {
        // 保存商品信息
        SkuInfo skuInfo = new SkuInfo();
        // 拷贝skuInfoVo --> skuInfo属性 (注意点: 属性名相同)
        BeanUtils.copyProperties(skuInfoVo, skuInfo);

        int insertSkuInfo = baseMapper.insert(skuInfo);

        // 保存商品海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        boolean savePosters = false;
        // 判断海报列表是否为空
        if (!CollectionUtils.isEmpty(skuPosterList)){
            // 遍历海报列表，设置商品id，因为在保存商品信息之后，才能拿到商品id
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            savePosters = skuPosterService.saveBatch(skuPosterList);
        }

        // 保存商品图片
        List<SkuImage> skuImageList = skuInfoVo.getSkuImagesList();
        boolean saveImages = false;
        if (!CollectionUtils.isEmpty(skuImageList)){
            // 遍历图片列表，设置商品id
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
            }
            saveImages = skuImageService.saveBatch(skuImageList);
        }

        // 保存商品平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        boolean saveAttrValues = false;
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            // 遍历属性列表，设置商品id
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
            }
            saveAttrValues = skuAttrValueService.saveBatch(skuAttrValueList);
        }

        return insertSkuInfo > 0 && savePosters && saveImages && saveAttrValues;
    }

    /**
     * 根据 id 获取商品信息
     *
     * @param id
     * @return
     */
    @Override
    public SkuInfoVo getSkuInfo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        // 根据 id 获取 sku 基本信息
        SkuInfo skuInfo = baseMapper.selectById(id);

        // 根据 id 获取 sku 图片列表
        List<SkuImage> skuImageList = skuImageService.getImageListBySkuId(id);

        // 根据 id 获取 sku 海报列表
        List<SkuPoster> skuPosterList = skuPosterService.getPosterListBySkuId(id);

        // 根据 id 获取 sku 平台属性列表
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getAttrValueListBySkuId(id);

        // 封装所有 sku 信息
        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);

        return skuInfoVo;
    }

    /**
     * 修改商品信息 (数据库操作比较多，一定要加Transactional注解开启事务功能！)
     *
     * @param skuInfoVo
     * @return
     */
    @Override
    @Transactional
    public boolean updateSkuInfo(SkuInfoVo skuInfoVo) {
        // 封装修改商品信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);

        int update = baseMapper.updateById(skuInfo);

        Long skuId = skuInfo.getId();

        boolean updateSkuInfo = update > 0;
        boolean updatePosters = false;
        boolean updateImages = false;
        boolean updateAttrValues = false;

        // 修改商品信息成功后，再修改商品图片、海报、属性
        if (updateSkuInfo){
            // 海报
            // 删除所有海报
            LambdaQueryWrapper<SkuPoster> wrapperPoster = new LambdaQueryWrapper<>();
            wrapperPoster.eq(SkuPoster::getSkuId, skuId);
            boolean removePoster = skuPosterService.remove(wrapperPoster);

            // 添加所有海报
            List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
            if (!CollectionUtils.isEmpty(skuPosterList)){
                for (SkuPoster skuPoster : skuPosterList) {
                    skuPoster.setSkuId(skuId);
                }
               updatePosters = skuPosterService.saveBatch(skuPosterList) && removePoster;
            }

            // 图片
            // 删除所有图片
            LambdaQueryWrapper<SkuImage> wrapperImage = new LambdaQueryWrapper<>();
            wrapperImage.eq(SkuImage::getSkuId, skuId);
            boolean removeImage = skuImageService.remove(wrapperImage);

            // 添加所有图片
            List<SkuImage> skuImageList = skuInfoVo.getSkuImagesList();
            if (!CollectionUtils.isEmpty(skuImageList)){
                for (SkuImage skuImage : skuImageList) {
                    skuImage.setSkuId(skuId);
                }
                updateImages = skuImageService.saveBatch(skuImageList) && removeImage;
            }

            // 属性
            // 删除所有属性
            LambdaQueryWrapper<SkuAttrValue> wrapperAttrValue = new LambdaQueryWrapper<>();
            wrapperAttrValue.eq(SkuAttrValue::getSkuId, skuId);
            boolean removeAttrValue = skuAttrValueService.remove(wrapperAttrValue);

            // 添加所有属性
            List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
            if (!CollectionUtils.isEmpty(skuAttrValueList)){
                for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                    skuAttrValue.setSkuId(skuId);
                }
                updateAttrValues = skuAttrValueService.saveBatch(skuAttrValueList) && removeAttrValue;
            }
        }

        return updateSkuInfo && updatePosters && updateImages && updateAttrValues;
    }

    /**
     * 商品审核
     *
     * @param skuId
     * @param status
     * @return
     */
    @Override
    public boolean check(Long skuId, Integer status) {
        SkuInfo skuInfo = new SkuInfo();

        skuInfo.setId(skuId);
        skuInfo.setCheckStatus(status);

        return baseMapper.updateById(skuInfo) > 0;
    }

    /**
     * 商品上架
     *
     * @param skuId
     * @param status
     * @return
     */
    @Override
    public boolean publish(Long skuId, Integer status) {
        if (status == 1) {
            // 上架
            SkuInfo skuInfo = baseMapper.selectById(skuId);

            skuInfo.setPublishStatus(status);

            // 整合mq将数据同步到es里面，另起线程发送数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_UPPER,
                    skuId);

            return baseMapper.updateById(skuInfo) > 0;
        } else {
            // 下架
            SkuInfo skuInfo = baseMapper.selectById(skuId);

            skuInfo.setPublishStatus(status);

            // 整合mq将数据同步到es里面，另起线程发送数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,
                    MqConst.ROUTING_GOODS_LOWER,
                    skuId);

            return baseMapper.updateById(skuInfo) > 0;
        }
    }

    /**
     * 设置SKU为新人专享状态
     *
     * @param skuId  SKU ID
     * @param status 新人专享状态：1-是，0-否
     * @return 是否设置成功
     */
    @Override
    public boolean isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfo = new SkuInfo();

        skuInfo.setId(skuId);
        skuInfo.setIsNewPerson(status);

        return baseMapper.updateById(skuInfo) > 0;
    }

    /**
     * 根据SKU ID列表批量查询SKU信息
     *
     * @param skuIdList SKU ID列表
     * @return SKU信息列表
     */
    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIdList) {
        List<SkuInfo> skuInfos = baseMapper.selectBatchIds(skuIdList);

        return skuInfos;
    }

    /**
     * 根据关键字模糊查询SKU信息
     *
     * @param keyword 查询关键字
     * @return 匹配的SKU信息列表
     */
    @Override
    public List<SkuInfo> getSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = baseMapper.selectList(
                new LambdaQueryWrapper<SkuInfo>()
                        .like(SkuInfo::getSkuName, keyword)
        );

        return skuInfoList;
    }

    /**
     * 查询最新上架的新人专享商品列表（最多返回3条记录）
     *
     * @return 新人专享商品列表
     */
    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        // 创建分页对象(只要前三个数据)
        Page<SkuInfo> page = new Page<>(1, 3);

        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfo::getIsNewPerson, 1); // 新人专享
        wrapper.eq(SkuInfo::getPublishStatus, 1); // 上架
        wrapper.orderByDesc(SkuInfo::getSort); // 按库存排序

        List<SkuInfo> skuInfoList = baseMapper.selectPage(page, wrapper).getRecords();

        return skuInfoList;
    }
}
