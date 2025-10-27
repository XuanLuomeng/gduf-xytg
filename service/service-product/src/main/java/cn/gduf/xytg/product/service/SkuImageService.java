package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.SkuImage;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品图片服务接口
 * @date 2025/10/22 21:18
 */
public interface SkuImageService extends IService<SkuImage> {
    /**
     * 根据skuId查询图片列表
     *
     * @param id
     * @return
     */
    List<SkuImage> getImageListBySkuId(Long id);
}
