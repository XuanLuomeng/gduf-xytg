package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.SkuAttrValueMapper;
import cn.gduf.xytg.product.service.SkuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.SkuAttrValue;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性值服务实现类
 * @date 2025/10/22 21:29
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue> implements SkuAttrValueService {
    /**
     * 根据skuId获取属性值列表
     *
     * @param id
     * @return
     */
    @Override
    public List<SkuAttrValue> getAttrValueListBySkuId(Long id) {
        LambdaQueryWrapper<SkuAttrValue> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SkuAttrValue::getSkuId, id);

        return baseMapper.selectList(wrapper);
    }
}
