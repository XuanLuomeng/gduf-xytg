package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.Category;
import cn.gduf.xytg.vo.product.CategoryQueryVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品三级分类服务接口
 * @date 2025/10/22 21:17
 */
public interface CategoryService extends IService<Category> {
    /**
     * 分页查询商品三级分类
     *
     * @param pageParam
     * @return
     */
    IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo);
}
