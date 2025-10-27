package com.gduf.xytg.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.gduf.xytg.model.acl.Admin;
import cn.gduf.xytg.vo.acl.AdminQueryVo;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户角色服务接口
 * @date 2025/10/20 21:43
 */
public interface AdminService extends IService<Admin> {
    IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo);
}
