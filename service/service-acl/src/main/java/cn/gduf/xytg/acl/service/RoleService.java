package com.gduf.xytg.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.acl.Role;
import cn.gduf.xytg.vo.acl.RoleQueryVo;

import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 角色服务接口
 * @date 2025/10/18 22:46
 */
public interface RoleService extends IService<Role> {
    /**
     * 角色列表（条件分页查询）
     */
    IPage<Role> selectRolePage(Page<Role> page, RoleQueryVo roleQueryVo);

    /**
     * 获取所有角色，和根据用户id查询用户分配角色列表
     */
    Map<String, Object> getRoleByAdminId(Long adminId);

    /**
     * 为用户进行分配
     */
    boolean saveAdminRole(Long adminId, Long[] roleIds);
}
