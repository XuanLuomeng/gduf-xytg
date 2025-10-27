package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.SkuAttrValue;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 属性值服务接口
 * @date 2025/10/22 21:18
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {
    /**
     * 根据skuId查询属性值列表
     *
     * @param id
     * @return
     */
    List<SkuAttrValue> getAttrValueListBySkuId(Long id);
}
