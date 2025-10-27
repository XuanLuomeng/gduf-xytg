package cn.gduf.xytg.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.sys.Region;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 地区服务接口
 * @date 2025/10/21 22:44
 */
public interface RegionService extends IService<Region> {
    /**
     * 根据关键词查询地区
     *
     * @param keyword
     * @return
     */
    List<Region> getRegionByKeyword(String keyword);

    /**
     * 根据父级id查询地区
     *
     * @param parentId
     * @return
     */
    List<Region> getRegionByParentId(String parentId);
}
