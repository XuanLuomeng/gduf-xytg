package com.gduf.xytg.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.acl.mapper.RoleMapper;
import com.gduf.xytg.acl.service.RoleService;
import com.gduf.xytg.model.acl.Role;
import com.gduf.xytg.vo.acl.RoleQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 角色服务实现类
 * @date 2025/10/18 22:47
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public IPage<Role> selectRolePage(Page<Role> page,
                                      RoleQueryVo roleQueryVo) {
        // 获取条件
        String roleName = roleQueryVo.getRoleName();

        // 创建 wrapper
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        // 判断角色名称是否存在
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }

        // 分页查询(baseMapper在ServiceImpl已自动注入)
        IPage<Role> rolePage = baseMapper.selectPage(page, wrapper);

        return rolePage;
    }
}
