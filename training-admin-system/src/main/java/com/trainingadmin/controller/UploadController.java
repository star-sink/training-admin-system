package com.trainingadmin.controller;

import com.trainingadmin.exception.BusinessException;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
public class UploadController {

    private final AliyunOSSOperator aliyunOSSOperator;

    public UploadController(AliyunOSSOperator aliyunOSSOperator) {
        this.aliyunOSSOperator = aliyunOSSOperator;
    }

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        String originalFilename = file.getOriginalFilename();
        log.info("文件上传：{}", originalFilename);

        // 校验图片扩展名，避免上传任意类型的文件
        String lowerName = originalFilename == null ? "" : originalFilename.toLowerCase();
        if (!(lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".png") || lowerName.endsWith(".gif")
                || lowerName.endsWith(".webp"))) {
            throw new BusinessException("仅支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        String url = aliyunOSSOperator.upload(file.getBytes(), originalFilename);
        log.info("文件上传 OSS 成功：{}", url);
        return Result.success(url);
    }
}
