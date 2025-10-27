package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.AttrGroupMapper;
import cn.gduf.xytg.product.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.AttrGroup;
import cn.gduf.xytg.vo.product.AttrGroupQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性分组服务实现类
 * @date 2025/10/22 21:26
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {
    @Override
    public IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo) {
        String name = attrGroupQueryVo.getName();
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(name)) {
            wrapper.like(AttrGroup::getName, name);
        }

        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<AttrGroup> findAllListAttrGroup() {
        QueryWrapper<AttrGroup> wrapper = new QueryWrapper<>();

        // 因为 AttrGroup 是继承了 Entity 抽象类
        // AttrGroup 本身不存在 id 属性，所以不能使用 AttrGroup::getId
        // 所以只能使用 QueryWrapper，指定 "id" 字符串
        wrapper.orderByDesc("id");

        return baseMapper.selectList(wrapper);
    }
}
