package com.gduf.xytg.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gduf.xytg.model.acl.Role;
import com.gduf.xytg.vo.acl.RoleQueryVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 角色服务接口
 * @date 2025/10/18 22:46
 */
public interface RoleService extends IService<Role> {
    IPage<Role> selectRolePage(Page<Role> page, RoleQueryVo roleQueryVo);
}
