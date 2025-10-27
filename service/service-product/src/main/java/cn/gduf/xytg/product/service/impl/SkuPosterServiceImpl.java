package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.SkuPosterMapper;
import cn.gduf.xytg.product.service.SkuPosterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.SkuPoster;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品海报服务实现类
 * @date 2025/10/22 21:31
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {
    /**
     * 根据商品id查询海报列表
     *
     * @param id
     * @return
     */
    @Override
    public List<SkuPoster> getPosterListBySkuId(Long id) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SkuPoster::getSkuId, id);

        return baseMapper.selectList(wrapper);
    }
}
