package cn.gduf.xytg.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.product.SkuPoster;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 商品海报服务接口
 * @date 2025/10/22 21:19
 */
public interface SkuPosterService extends IService<SkuPoster> {
    /**
     * 根据skuId查询海报列表
     *
     * @param id
     * @return
     */
    List<SkuPoster> getPosterListBySkuId(Long id);
}
