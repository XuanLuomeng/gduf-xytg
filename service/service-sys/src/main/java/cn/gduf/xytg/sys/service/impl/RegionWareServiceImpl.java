package cn.gduf.xytg.sys.service.impl;

import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.sys.mapper.RegionWareMapper;
import cn.gduf.xytg.sys.service.RegionWareService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.sys.RegionWare;
import cn.gduf.xytg.vo.sys.RegionWareQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 城市仓库关联服务接口实现类
 * @date 2025/10/21 22:46
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {
    /**
     * 分页查询城市仓库关联
     *
     * @param pageParam
     * @param regionWareQueryVo
     * @return
     */
    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam,
                                                  RegionWareQueryVo regionWareQueryVo) {
        // 获取条件值
        String keyword = regionWareQueryVo.getKeyword();

        // 判断条件值是否存在
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            // 模糊查询
            wrapper.like(RegionWare::getRegionName, keyword)
                    .or().like(RegionWare::getWareName, keyword);
        }

        // 调用方法实现条件分页查询
        IPage<RegionWare> pageModel = baseMapper.selectPage(pageParam, wrapper);

        return pageModel;
    }

    /**
     * 添加城市仓库关联
     *
     * @param regionWare
     * @return
     */
    @Override
    public boolean saveRegionWare(RegionWare regionWare) {
        // 判断是否已经添加
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());

        // 使用 selectCount 也可以
        RegionWare one = baseMapper.selectOne(wrapper);

        // 已存在
        if (one != null) {
            // 抛出异常
            throw new XytgException(ResultCodeEnum.REGION_OPEN);
        }

        return baseMapper.insert(regionWare) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);

        // 设置状态
        regionWare.setStatus(status);

        return baseMapper.updateById(regionWare) > 0;
    }
}
