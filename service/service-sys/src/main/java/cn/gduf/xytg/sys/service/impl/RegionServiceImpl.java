package cn.gduf.xytg.sys.service.impl;

import cn.gduf.xytg.sys.mapper.RegionMapper;
import cn.gduf.xytg.sys.service.RegionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.model.sys.Region;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 地区服务接口实现类
 * @date 2025/10/21 22:46
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
}
