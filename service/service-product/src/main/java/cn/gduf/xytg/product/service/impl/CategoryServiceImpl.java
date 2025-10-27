package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.product.mapper.CategoryMapper;
import cn.gduf.xytg.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.vo.product.CategoryQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品分类服务实现类
 * @date 2025/10/22 21:28
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 分页查询商品分类
     *
     * @param pageParam
     * @param categoryQueryVo
     * @return
     */
    @Override
    public IPage<Category> selectPageCategory(Page<Category> pageParam,
                                              CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like(Category::getName, name);
        }

        return baseMapper.selectPage(pageParam, queryWrapper);
    }
}
