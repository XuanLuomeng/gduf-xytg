package cn.gduf.xytg.product.service.impl;

import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.product.service.FileUploadService;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 文件上传服务实现类
 * @date 2025/10/22 21:29
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    MinioClient minioClient;

    @Autowired
    private FileUploadService currentProxy;

    // 桶名称
    @Value("${minio.bucket.files}")
    private String bucket_files;

    // 获取MinIO的公开URL
    @Value("${minio.public-url}")
    private String minioPublicUrl;

    @Override
    public String uploadFile(MultipartFile fileData) {
        //创建一个临时文件
        File tempFile = null;
        try {
            tempFile = File.createTempFile("minio", ".temp");
            fileData.transferTo(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        String localFilePath = tempFile.getAbsolutePath();

        //文件名
        String filename = fileData.getOriginalFilename();
        //先得到扩展名
        String extension = "";
        if (filename != null && filename.lastIndexOf(".") > 0) {
            extension = filename.substring(filename.lastIndexOf("."));
        }
        //子目录
        String defaultFolderPath = getDefaultFolderPath();
        //文件的md5值
        String fileMd5 = getFileMd5(new File(localFilePath));

        String objectName = defaultFolderPath + fileMd5 + extension;

        //得到mimeType
        String mimeType = getMimeType(extension);

        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_files, objectName);

        // 文件上传成功后返回访问路径
        if (result) {
            return minioPublicUrl + "/" + bucket_files + "/" + objectName;
        } else {
            throw new XytgException(ResultCodeEnum.UPLOAD_FILE_ERROR);
        }
    }

    /**
     * 添加媒体文件到MinIO
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      媒体文件类型
     * @param bucket        桶名称
     * @param objectName    对象名称
     * @return 是否添加成功
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)//桶
                    .filename(localFilePath) //指定本地文件路径
                    .object(objectName)//对象名 放在子目录下
                    .contentType(mimeType)//设置媒体文件类型
                    .build();
            //上传文件
            ObjectWriteResponse response = minioClient.uploadObject(uploadObjectArgs);
            return response != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据扩展名得到MIME类型
     *
     * @param extension 文件扩展名
     * @return MIME类型
     */
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }

        // 根据扩展名获取 MIME 类型
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 通用 MIME 类型，字节流

        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                mimeType = MediaType.IMAGE_JPEG_VALUE;
                break;
            case ".png":
                mimeType = MediaType.IMAGE_PNG_VALUE;
                break;
            case ".gif":
                mimeType = MediaType.IMAGE_GIF_VALUE;
                break;
            case ".pdf":
                mimeType = MediaType.APPLICATION_PDF_VALUE;
                break;
            case ".txt":
                mimeType = MediaType.TEXT_PLAIN_VALUE;
                break;
            case ".html":
            case ".htm":
                mimeType = MediaType.TEXT_HTML_VALUE;
                break;
            case ".json":
                mimeType = MediaType.APPLICATION_JSON_VALUE;
                break;
            case ".xml":
                mimeType = MediaType.APPLICATION_XML_VALUE;
                break;
            default:
                // 保持默认的字节流类型
                break;
        }

        return mimeType;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file 文件
     * @return 文件的MD5值
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取默认的文件夹路径
     *
     * @return 文件夹路径
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }
}
