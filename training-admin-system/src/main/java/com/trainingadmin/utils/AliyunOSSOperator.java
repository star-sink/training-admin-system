package com.trainingadmin.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.aliyun.sdk.service.oss2.transport.ByteArrayBinaryData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOSSOperator {

    // 1. 使用 @Autowired 注入配置类
    @Autowired
    private AliyunOSSProperties aliyunOSSProperties;

    // 2. 去掉 final 关键字，因为不在构造方法里赋值了
    private OSSClient ossClient;

    // 3. 务必删掉 public AliyunOSSOperator() 构造方法！
    // 使用 @PostConstruct，保证 aliyunOSSProperties 注入完成后，再初始化客户端
    @PostConstruct
    public void init() {
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        this.ossClient = OSSClient.newBuilder()
                .endpoint(aliyunOSSProperties.getEndpoint()) // 从配置类获取
                .credentialsProvider(credentialsProvider)
                .region(aliyunOSSProperties.getRegion())     // 从配置类获取
                .build();
    }

    public String upload(byte[] content, String originalFilename) throws Exception {
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = dir + "/" + newFileName;

        BinaryData body = new ByteArrayBinaryData(content);

        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(aliyunOSSProperties.getBucketName()) // 从配置类获取
                .key(objectName)
                .body(body)
                .build();

        ossClient.putObject(request);

        // 拼接返回的访问 URL
        return aliyunOSSProperties.getEndpoint().split("//")[0] + "//"
                + aliyunOSSProperties.getBucketName() + "."
                + aliyunOSSProperties.getEndpoint().split("//")[1] + "/" + objectName;
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (ossClient != null) {
            ossClient.close();
        }
    }
}