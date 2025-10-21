package com.gduf.xytg.acl.controll;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.common.utils.MD5;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gduf.xytg.acl.service.AdminRoleService;
import com.gduf.xytg.acl.service.AdminService;
import com.gduf.xytg.acl.service.RoleService;
import com.gduf.xytg.model.acl.Admin;
import com.gduf.xytg.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 用户接口
 * @date 2025/10/20 21:41
 */
@Api("用户接口")
@RestController
@RequestMapping("/admin/acl/user")
//@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    RoleService roleService;

    @Autowired
    private AdminRoleService adminRoleService;

    /**
     * 为用户进行角色分配
     *
     * @param adminId
     * @param roleIdList
     * @return
     */
    @ApiOperation("为用户进行角色分配")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId,
                           @RequestBody Long[] roleIdList) {
        boolean result = roleService.saveAdminRole(adminId, roleIdList);
        if (result) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 获取所有角色，和根据用户id查询用户分配角色列表
     *
     * @param adminId
     * @return
     */
    @ApiOperation("获取用户角色")
    @GetMapping("toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId) {
        // 返回map集合包含两部分数据：所有角色 和 为用户分配角色列表
        Map<String, Object> resultMap = roleService.getRoleByAdminId(adminId);
        return Result.ok(resultMap);
    }

    /**
     * 用户列表
     *
     * @param current
     * @param limit
     * @param adminQueryVo
     * @return
     */
    @ApiOperation("用户列表")
    @GetMapping("{current}/{limit}")
    public Result list(@PathVariable Long current,
                       @PathVariable Long limit,
                       AdminQueryVo adminQueryVo) {
        Page<Admin> pageParam = new Page<>(current, limit);

        IPage<Admin> pageModel = adminService.selectPageUser(pageParam, adminQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.fail(null);
        } else {
            return Result.ok(admin);
        }
    }

    /**
     * 添加用户
     *
     * @param admin
     * @return
     */
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin) {
        // 获取原密码用于密码加密
        String password = admin.getPassword();

        // 密码加密
        String md5Password = MD5.encrypt(password);

        // 设置加密后的密码
        admin.setPassword(md5Password);

        boolean save = adminService.save(admin);

        if (save) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 修改用户
     *
     * @param admin
     * @return
     */
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin) {
        // 判断密码是否为空，不为空则进行密码加密
        if (!StringUtils.isEmpty(admin.getPassword())) {
            // 获取原密码用于密码加密
            String password = admin.getPassword();

            // 密码加密
            String md5Password = MD5.encrypt(password);

            // 获取加密后的密码
            admin.setPassword(md5Password);
        }

        boolean update = adminService.updateById(admin);
        if (update) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 根据id删除用户
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = adminService.removeById(id);
        if (remove) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean remove = adminService.removeByIds(idList);
        if (remove) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }
}
