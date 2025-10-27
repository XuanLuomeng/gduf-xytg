package cn.gduf.xytg.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 文件上传服务接口
 * @date 2025/10/22 21:17
 */
public interface FileUploadService {
    /**
     * 文件上传
     *
     * @param fileName
     * @return
     */
    String uploadFile(MultipartFile fileName);
}
