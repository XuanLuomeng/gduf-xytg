package cn.gduf.xytg.sys.service.impl;

import cn.gduf.xytg.sys.mapper.RegionMapper;
import cn.gduf.xytg.sys.service.RegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.sys.Region;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 地区服务接口实现类
 * @date 2025/10/21 22:46
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    /**
     * 根据关键词查询
     *
     * @param keyword
     * @return
     */
    @Override
    public List<Region> getRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(Region::getName, keyword);

        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据父级id查询
     *
     * @param parentId
     * @return
     */
    @Override
    public List<Region> getRegionByParentId(String parentId) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Region::getParentId, parentId);

        return baseMapper.selectList(wrapper);
    }
}
