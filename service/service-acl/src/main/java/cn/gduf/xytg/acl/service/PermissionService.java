package com.gduf.xytg.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.acl.Permission;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 权限服务接口
 * @date 2025/10/21 20:14
 */
public interface PermissionService extends IService<Permission> {
    /**
     * 查询所有菜单
     *
     * @return
     */
    List<Permission> queryAllPermission();

    /**
     * 根据id递归删除菜单
     *
     * @param id
     * @return
     */
    boolean removeChildById(Long id);

    /**
     * 根据角色id获取角色菜单
     *
     * @param roleId
     * @return
     */
    Map<String, Object> getPermissionByRoleId(Long roleId);

    /**
     * 为角色分配菜单
     *
     * @param roleId
     * @param permissionId
     * @return
     */
    boolean saveRolePermission(String roleId, String[] permissionId);
}
