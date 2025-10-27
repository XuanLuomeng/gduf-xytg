package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.AttrMapper;
import cn.gduf.xytg.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.Attr;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性表服务实现类
 * @date 2025/10/22 21:27
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
    /**
     * 根据分组id查询属性列表
     *
     * @param groupId
     * @return
     */
    @Override
    public List<Attr> getAttrListByGroupId(Long groupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Attr::getAttrGroupId, groupId);

        List<Attr> list = baseMapper.selectList(wrapper);

        return list;
    }
}
