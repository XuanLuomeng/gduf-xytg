package cn.gduf.xytg.sys.service.impl;

import cn.gduf.xytg.sys.mapper.WareMapper;
import cn.gduf.xytg.sys.service.WareService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.sys.Ware;
import cn.gduf.xytg.vo.product.WareQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 仓库表服务接口实现类
 * @date 2025/10/21 22:46
 */
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware> implements WareService {
    @Override
    public IPage<Ware> selectPageRegion(Page<Ware> pageParam, WareQueryVo wareQueryVo) {
        LambdaQueryWrapper<Ware> wrapper = new LambdaQueryWrapper<>();

        if (wareQueryVo != null){
            wrapper.like(!StringUtils.isEmpty(wareQueryVo.getName()), Ware::getName, wareQueryVo.getName());
            wrapper.like(!StringUtils.isEmpty(wareQueryVo.getProvince()), Ware::getProvince, wareQueryVo.getProvince());
            wrapper.like(!StringUtils.isEmpty(wareQueryVo.getCity()), Ware::getCity, wareQueryVo.getCity());
            wrapper.like(!StringUtils.isEmpty(wareQueryVo.getDistrict()), Ware::getDistrict, wareQueryVo.getDistrict());
        }

        return baseMapper.selectPage(pageParam, wrapper);
    }
}
