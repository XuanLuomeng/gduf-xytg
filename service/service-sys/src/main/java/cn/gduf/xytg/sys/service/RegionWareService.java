package cn.gduf.xytg.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gduf.xytg.model.sys.RegionWare;
import com.gduf.xytg.vo.sys.RegionWareQueryVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 城市仓库关联服务接口
 * @date 2025/10/21 22:44
 */
public interface RegionWareService extends IService<RegionWare> {
    /**
     * 分页查询城市仓库关联
     *
     * @param pageParam
     * @param regionWareQueryVo
     * @return
     */
    IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo);

    /**
     * 新增城市仓库关联
     *
     * @param regionWare
     * @return
     */
    boolean saveRegionWare(RegionWare regionWare);

    /**
     * 修改仓库状态
     *
     * @param id
     * @param status
     * @return
     */
    boolean updateStatus(Long id, Integer status);
}
