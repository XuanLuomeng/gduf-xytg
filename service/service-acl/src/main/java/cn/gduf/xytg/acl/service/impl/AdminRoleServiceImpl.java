package com.gduf.xytg.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.acl.mapper.AdminRoleMapper;
import com.gduf.xytg.acl.service.AdminRoleService;
import com.gduf.xytg.model.acl.AdminRole;
import org.springframework.stereotype.Service;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户分配角色服务接口实现类
 * @date 2025/10/21 18:12
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
