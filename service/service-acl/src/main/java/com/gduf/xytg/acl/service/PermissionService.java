package com.gduf.xytg.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gduf.xytg.model.acl.Permission;

import java.util.List;

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
}
