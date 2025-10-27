package cn.gduf.xytg.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.sys.Ware;
import cn.gduf.xytg.vo.product.WareQueryVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 仓库表服务接口
 * @date 2025/10/21 22:45
 */
public interface WareService extends IService<Ware> {
    /**
     * 分页查询仓库表
     *
     * @param pageParam
     * @param wareQueryVo
     * @return
     */
    IPage<Ware> selectPageRegion(Page<Ware> pageParam, WareQueryVo wareQueryVo);
}
