package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.AttrGroup;
import cn.gduf.xytg.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性分组服务接口
 * @date 2025/10/22 21:16
 */
public interface AttrGroupService extends IService<AttrGroup> {
    /**
     * 分页查询属性分组
     *
     * @param pageParam
     * @param attrGroupQueryVo
     * @return
     */
    IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo);

    /**
     * 查询所有属性分组
     *
     * @return
     */
    List<AttrGroup> findAllListAttrGroup();
}
