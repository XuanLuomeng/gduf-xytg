package com.gduf.xytg.acl.controll;

import cn.gduf.xytg.common.result.Result;
import com.gduf.xytg.acl.service.PermissionService;
import cn.gduf.xytg.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 菜单管理 前端控制器
 * @date 2025/10/21 20:16
 */
@RestController
@RequestMapping("/admin/acl/permission")
@Api(tags = "菜单管理")
//@CrossOrigin
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    /**
     * 查询所有菜单
     *
     * @return
     */
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.queryAllPermission();
        return Result.ok(list);
    }

    /**
     * 添加菜单
     *
     * @param permission
     * @return
     */
    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission){
        boolean save = permissionService.save(permission);

        return save ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 修改菜单
     *
     * @param permission
     * @return
     */
    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result update(@RequestBody Permission permission){
        boolean update = permissionService.updateById(permission);

        return update ? Result.ok(null) : Result.fail(null);
    }

    /**
     * 递归删除菜单
     *
     * @param id
     * @return
     */
    @ApiOperation("递归删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean remove = permissionService.removeChildById(id);

        return remove ? Result.ok(null) : Result.fail(null);
    }
}
