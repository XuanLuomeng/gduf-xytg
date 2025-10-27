package com.gduf.xytg.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.acl.mapper.RoleMapper;
import com.gduf.xytg.acl.service.AdminRoleService;
import com.gduf.xytg.acl.service.RoleService;
import cn.gduf.xytg.model.acl.AdminRole;
import cn.gduf.xytg.model.acl.Role;
import cn.gduf.xytg.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 角色服务实现类
 * @date 2025/10/18 22:47
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private AdminRoleService adminRoleService;

    /**
     * 角色列表（条件分页查询）
     *
     * @param page
     * @param roleQueryVo
     * @return
     */
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

    /**
     * 获取所有角色，和根据用户id查询用户分配角色列表
     *
     * @param adminId
     * @return
     */
    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        // 获取所有角色
        List<Role> allRolesList = baseMapper.selectList(null);

        // 根据用户id查询用户分配角色列表
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();

        // 条件
        wrapper.eq(AdminRole::getAdminId, adminId);

        // 查询
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);

        List<Long> adminRoleIds = adminRoleList
                .stream()
                .map(AdminRole::getRoleId)
                .collect(Collectors.toList());

        List<Role> assignRoleList = allRolesList
                .stream()
                .filter(item -> adminRoleIds.contains(item.getId()))
                .collect(Collectors.toList());

        //封装到map，返回
        Map<String, Object> roleMap = new HashMap<>();

        //所有角色列表
        roleMap.put("allRolesList", allRolesList);

        //用户分配角色列表
        roleMap.put("assignRoles", assignRoleList);

        return roleMap;
    }

    /**
     * 为用户进行分配
     *
     * @param adminId
     * @param roleIds
     * @return
     */
    @Override
    // 因为批量保存同时进行了删除操作，需要使用事务注解
    @Transactional
    public boolean saveAdminRole(Long adminId, Long[] roleIds) {
        // 删除用户已经分配过的角色数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        boolean remove = adminRoleService.remove(wrapper);

        // 重新分配
        List<AdminRole> list = new ArrayList<>();

        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }

        boolean saveBatch = adminRoleService.saveBatch(list);

        return remove && saveBatch;
    }
}
