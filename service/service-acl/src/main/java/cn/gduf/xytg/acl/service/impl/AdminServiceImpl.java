package com.gduf.xytg.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gduf.xytg.acl.mapper.AdminMapper;
import com.gduf.xytg.acl.service.AdminService;
import cn.gduf.xytg.model.acl.Admin;
import cn.gduf.xytg.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户服务接口实现类
 * @date 2025/10/20 21:43
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo) {
        String userName = adminQueryVo.getUsername();
        String name = adminQueryVo.getName();

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();

        // 判断用户名称是否存在
        if (!StringUtils.isEmpty(userName)) {
            wrapper.like(Admin::getUsername, userName);
        }

        // 判断用昵称是否存在
        if (!StringUtils.isEmpty(name)) {
            wrapper.like(Admin::getName, name);
        }

        // 分页查询
        return baseMapper.selectPage(pageParam, wrapper);
    }
}
