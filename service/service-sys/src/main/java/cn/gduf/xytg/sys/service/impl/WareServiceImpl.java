package cn.gduf.xytg.sys.service.impl;

import cn.gduf.xytg.sys.mapper.WareMapper;
import cn.gduf.xytg.sys.service.WareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.model.sys.Ware;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 仓库表服务接口实现类
 * @date 2025/10/21 22:46
 */
@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware> implements WareService {
}
