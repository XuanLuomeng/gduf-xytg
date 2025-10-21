package com.gduf.xytg.acl.utils;

import com.gduf.xytg.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 根据权限数据构建菜单数据
 * @date 2025/10/21 20:57
 */
public class PermissionHelper {

    /**
     * 构建权限树结构，将扁平的权限列表转换为具有层级关系的树形结构
     *
     * @param allPermissionList 所有权限信息的列表
     * @return 构建完成的权限树结构列表
     */
    public static List<Permission> buildPermssion(List<Permission> allPermissionList) {
        // 创建最终数据封装List
        List<Permission> result = new ArrayList<>();

        // 遍历所有菜单
        for (Permission permission : allPermissionList) {
            // 判断当前菜单是否是一级菜单
            if (permission.getPid() == 0) {
                // 一级菜单
                permission.setLevel(1);
                // 递归获取当前菜单下的所有子菜单
                result.add(findChildren(permission, allPermissionList));
            }
        }
        return null;
    }

    /**
     * 递归获取当前菜单下的所有子菜单
     *
     * @param permission 当前权限节点
     * @param allPermissionList 所有权限信息的列表
     * @return 包含子菜单的权限节点
     */
    private static Permission findChildren(Permission permission, List<Permission> allPermissionList) {
        // 初始化子菜单集合
        permission.setChildren(new ArrayList<Permission>());

        // 遍历所有菜单
        for (Permission it : allPermissionList) {
            // 判断当前菜单是否是当前菜单的子菜单
            if (permission.getId().longValue() == it.getPid().longValue()) {
                // 设置子菜单层级
                int level = permission.getLevel() + 1;
                it.setLevel(level);

                // 避免向空子菜单添加内容
                if (permission.getChildren() == null){
                    permission.setChildren(new ArrayList<Permission>());
                }

                // 递归查找并添加子菜单
                permission.getChildren().add(findChildren(it, allPermissionList));
            }
        }

        return permission;
    }
}
