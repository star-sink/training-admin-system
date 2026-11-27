package com.trainingadmin.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.aliyun.sdk.service.oss2.transport.ByteArrayBinaryData;
import com.trainingadmin.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传组件
 *
 * <p>客户端在 {@code @PostConstruct} 中初始化，在 {@code @PreDestroy} 中关闭。
 * AccessKey 通过环境变量 {@code OSS_ACCESS_KEY_ID}、{@code OSS_ACCESS_KEY_SECRET} 注入。
 * 未配置 OSS 时应用仍可正常启动，仅上传接口不可用。</p>
 */
@Slf4j
@Component
public class AliyunOSSOperator {

    private final AliyunOSSProperties properties;

    private OSSClient ossClient;

    public AliyunOSSOperator(AliyunOSSProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (!isConfigured()) {
            log.warn("未配置阿里云 OSS（endpoint/bucketName/region 为空），文件上传功能不可用");
            return;
        }
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
        this.ossClient = OSSClient.newBuilder()
                .endpoint(properties.getEndpoint())
                .credentialsProvider(credentialsProvider)
                .region(properties.getRegion())
                .build();
        log.info("阿里云 OSS 客户端初始化完成");
    }

    /**
     * 上传文件，返回可访问的 URL
     *
     * @param content          文件字节数组
     * @param originalFilename 原始文件名，用于提取扩展名
     * @return 文件访问地址
     */
    public String upload(byte[] content, String originalFilename) {
        if (ossClient == null) {
            throw new BusinessException("文件上传服务未配置，请联系管理员");
        }
        String extension = getExtension(originalFilename);
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String objectName = dir + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(properties.getBucketName())
                .key(objectName)
                .body(new ByteArrayBinaryData(content))
                .build();
        ossClient.putObject(request);

        return buildUrl(objectName);
    }

    private boolean isConfigured() {
        return StringUtils.hasText(properties.getEndpoint())
                && StringUtils.hasText(properties.getBucketName())
                && StringUtils.hasText(properties.getRegion());
    }

    /**
     * 提取扩展名；没有扩展名时返回空字符串，避免 substring(-1) 异常
     */
    private String getExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dotIndex).toLowerCase();
    }

    private String buildUrl(String objectName) {
        String endpoint = properties.getEndpoint();
        int schemeIndex = endpoint.indexOf("://");
        if (schemeIndex < 0) {
            return properties.getBucketName() + "." + endpoint + "/" + objectName;
        }
        String scheme = endpoint.substring(0, schemeIndex);
        String host = endpoint.substring(schemeIndex + 3);
        return scheme + "://" + properties.getBucketName() + "." + host + "/" + objectName;
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (ossClient != null) {
            ossClient.close();
        }
    }
}
