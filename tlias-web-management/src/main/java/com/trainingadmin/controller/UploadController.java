package com.trainingadmin.controller;

import com.trainingadmin.pojo.Result;
import com.trainingadmin.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    /**
     * 本地磁盘存储方案
     */
/*    @PostMapping("/upload")
    public Result upload(String name,Integer age,MultipartFile file) throws IOException {
     log.info("接受参数:{},{},{}",name,age,file);
     //获取原始文件名
     String originalFilename= file.getOriginalFilename();

     //新的文件名
     String newFileName= UUID.randomUUID().toString()+".png";

     //保存文件
     file.transferTo(new File("E:/trainingadmin/"+originalFilename));
     return Result.success();
    }*/
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        log.info("文件上传：{}",file.getOriginalFilename());

        //将文件交给OSS存储管理
        String url= aliyunOSSOperator.upload(file.getBytes(),file.getOriginalFilename());

        //将文件交给OSS存储管理
        log.info("文件上传OSS{}",url);

        return Result.success(url);
    }
}
