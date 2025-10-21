package com.gduf.xytg.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.acl.mapper.PermissionMapper;
import com.gduf.xytg.acl.service.PermissionService;
import com.gduf.xytg.acl.service.RolePermissionService;
import com.gduf.xytg.acl.utils.PermissionHelper;
import com.gduf.xytg.model.acl.Permission;
import com.gduf.xytg.model.acl.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 菜单服务实现类
 * @date 2025/10/21 20:14
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService{
    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public List<Permission> queryAllPermission() {
        // 获取所有菜单
        List<Permission> allPermissionList = baseMapper.selectList(null);

        // 转换要求数据格式
        List<Permission> result = PermissionHelper.buildPermssion(allPermissionList);

        return result;
    }

    @Override
    public boolean removeChildById(Long id) {
        // 封装所有要删除菜单的id
        List<Long> idList = new ArrayList<>();

        // 根据当前菜单id，获取当前菜单下所有子菜单(子菜单下还有，都需要获取出来)
        this.getAllPermissionId(id, idList);

        // 删除当前菜单
        idList.add(id);

        int result = baseMapper.deleteBatchIds(idList);

        return result > 0;
    }

    /**
     * 根据角色id获取角色菜单
     *
     * @param roleId
     * @return
     */
    @Override
    public Map<String, Object> getPermissionByRoleId(Long roleId) {
        // 获取所有菜单
        List<Permission> allPermissionList = queryAllPermission();

        // 根据角色id查询角色分配菜单列表
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);

        // 查询
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);

        List<Long> rolePermissionIds = rolePermissionList
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        List<Permission> assignPermissionList = allPermissionList
                .stream()
                .filter(item -> rolePermissionIds.contains(item.getId()))
                .collect(Collectors.toList());

        // 封装到map，返回
        Map<String, Object> result = new HashMap<>();

        result.put("allPermission", allPermissionList);

        result.put("assignPermission", assignPermissionList);

        return result;
    }

    /**
     * 为角色分配菜单
     *
     * @param roleId
     * @param permissionId
     * @return
     */
    @Override
    @Transactional
    public boolean saveRolePermission(String roleId, String[] permissionId) {
        // 删除当前角色已经分配过的菜单数据
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);

        boolean remove = rolePermissionService.remove(wrapper);

        // 重新分配
        List<RolePermission> list = new ArrayList<>();
        for (String id : permissionId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(Long.parseLong(roleId));
            rolePermission.setPermissionId(Long.parseLong(id));
            list.add(rolePermission);
        }

        boolean saveBatch = rolePermissionService.saveBatch(list);

        return remove && saveBatch;
    }

    /**
     * 递归获取当前菜单下的所有子菜单
     *
     * @param id 当前菜单id
     * @param idList 封装所有要删除菜单的id
     */
    private void getAllPermissionId(Long id, List<Long> idList) {
        // 获取当前菜单下的所有子菜单
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);

        List<Permission> childList = baseMapper.selectList(wrapper);

        // 判断当前菜单下是否有子菜单
        childList.stream().forEach(item -> {
            // 封装当前菜单下的所有子菜单的id
            idList.add(item.getId());

            // 递归获取当前菜单下的所有子菜单
            this.getAllPermissionId(item.getId(), idList);
        });
    }
}
