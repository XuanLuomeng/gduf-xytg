package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.Attr;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性表服务接口
 * @date 2025/10/22 21:16
 */
public interface AttrService extends IService<Attr> {
    /**
     * 根据分组id查询属性列表
     *
     * @param groupId
     * @return
     */
    List<Attr> getAttrListByGroupId(Long groupId);
}
