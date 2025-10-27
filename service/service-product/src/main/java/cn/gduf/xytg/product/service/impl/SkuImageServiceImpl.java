package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.SkuImageMapper;
import cn.gduf.xytg.product.service.SkuImageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.SkuImage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品图片服务实现类
 * @date 2025/10/22 21:30
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {
    /**
     * 根据skuId查询图片列表
     *
     * @param id
     * @return
     */
    @Override
    public List<SkuImage> getImageListBySkuId(Long id) {
        LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SkuImage::getSkuId, id);

        return baseMapper.selectList(wrapper);
    }
}
