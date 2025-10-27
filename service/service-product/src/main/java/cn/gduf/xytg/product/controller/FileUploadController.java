package cn.gduf.xytg.product.controller;

import cn.gduf.xytg.common.result.Result;
import cn.gduf.xytg.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 文件上传控制器
 * @date 2025/10/22 21:33
 */
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("admin/product")
//@CrossOrigin
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 图片上传
     *
     * @param fileName
     * @return
     */
    @ApiOperation("图片上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile fileName) {
        String url = fileUploadService.uploadFile(fileName);
        return Result.ok(url);
    }
}
