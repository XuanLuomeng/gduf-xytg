package com.gduf.xytg.acl.controll;

import cn.gduf.xytg.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gduf.xytg.acl.service.PermissionService;
import com.gduf.xytg.acl.service.RoleService;
import cn.gduf.xytg.model.acl.Role;
import cn.gduf.xytg.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 角色接口
 * @date 2025/10/18 22:44
 */
@Api(tags = "角色接口")
@RestController
@RequestMapping("/admin/acl/role")
//@CrossOrigin //跨域
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 为角色分配菜单
     *
     * @param roleId
     * @param permissionId
     * @return
     */
    @ApiOperation("为角色进行菜单分配")
    @PostMapping("doAssign")
    public Result doAssign(String roleId, String[] permissionId) {
        boolean result = permissionService.saveRolePermission(roleId, permissionId);
        return result ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 获取角色菜单
     *
     * @param roleId
     * @return
     */
    @ApiOperation("获取角色菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        Map<String, Object> resultMap = permissionService.getPermissionByRoleId(roleId);
        return Result.ok(resultMap);
    }

    /**
     * 角色列表（条件分页查询，支持用户名模糊查询）
     *
     * @param current
     * @param limit
     * @param roleQueryVo
     * @return
     */
    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{limit}")
    public Result pageList(@PathVariable Long current,
                           @PathVariable Long limit,
                           RoleQueryVo roleQueryVo) {
        //1 创建page对象，传递当前页和每页记录数
        // current：当前页
        // limit: 每页显示记录数
        Page<Role> page = new Page<>(current, limit);

        //2 调用service方法实现条件分页查询，返回分页对象
        IPage<Role> pageModel = roleService.selectRolePage(page, roleQueryVo);

        return Result.ok(pageModel);
    }

    /**
     * 根据id查询角色
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role) {
        boolean save = roleService.save(role);
        if (save) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 修改角色
     *
     * @param role
     * @return
     */
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role) {
        boolean update = roleService.updateById(role);
        if (update) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 根据id删除角色
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean remove = roleService.removeById(id);
        if (remove) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 批量删除角色
     * json数组[1,2,3]  --- java的list集合
     *
     * @param idList
     * @return
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean remove = roleService.removeByIds(idList);
        if (remove) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }
}
