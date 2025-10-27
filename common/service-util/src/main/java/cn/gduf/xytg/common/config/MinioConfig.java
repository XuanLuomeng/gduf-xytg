package cn.gduf.xytg.common.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description minio分布式文件系统配置
 * @date 2025/10/22 22:36
 */
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 创建并配置Minio客户端Bean
     *
     * @return MinioClient 配置好的Minio客户端实例
     */
    @Bean
    public MinioClient minioClient() {
        // 构建Minio客户端，配置连接地址和认证信息
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        return minioClient;
    }
}
