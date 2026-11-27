package com.trainingadmin;

// --- 核心导包区 ---
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest; // V2 版本执行上传必需的请求类
import com.aliyun.sdk.service.oss2.transport.BinaryData;

// --- 本地文件操作导包区 ---
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Demo {
    public static void main(String[] args) {
        // 1. 从环境变量中加载凭证信息 (前提：你必须已经配置了 OSS_ACCESS_KEY_ID 和 SECRET)
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();

        // 2. 填写 Bucket 所在地域 ID（华北2 北京）
        String region = "cn-beijing";

        // 3. 填写标准公网 Endpoint（你截图里的外网访问地址）
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";

        // --- 准备上传所需的参数 ---
        String bucketName = "java-ai-jihai"; // 你的 Bucket 名称
        String objectKey = "001.png";        // 上传到 OSS 后保存的文件名
        String localFilePath = "E:\\trainingadmin\\1.png"; // 你本地电脑上的文件路径

        // 4. 创建 OSS 客户端 和 读取本地文件流
        // 注意：这里用了 try( ... )，它会自动帮我们关闭网络连接和文件流，防止内存泄漏
        try (
                // 4.1 创建客户端（注意：去掉了 .useCName，因为你是标准域名，不是自定义域名）
                OSSClient client = OSSClient.newBuilder()
                        .credentialsProvider(credentialsProvider)
                        .region(region)
                        .endpoint(endpoint)
                        .build();

                // 4.2 这就是你要的“路径引入”操作：把本地路径变成文件，再变成数据流供 SDK 读取
                InputStream fileStream = new FileInputStream(new File(localFilePath))
        ) {

            System.out.println("客户端创建成功，开始准备上传...");

            // 5. 构建上传请求
            PutObjectRequest request = PutObjectRequest.newBuilder()
                    .bucket(bucketName)      // 指定放到哪个桶
                    .key(objectKey)          // 指定存成什么名字
                    .body(BinaryData.fromStream(fileStream))        // 把刚才读取的文件流放进去
                    .build();

            // 6. 执行上传操作（真正发请求的代码）
            client.putObject(request);

            System.out.println("🎉 文件上传成功！请去阿里云 OSS 控制台查看。");

        } catch (Exception e) {
            // 如果报错，这里会打印详细原因
            System.err.println("❌ 报错了，错误信息如下：");
            e.printStackTrace();
        }
    }
}