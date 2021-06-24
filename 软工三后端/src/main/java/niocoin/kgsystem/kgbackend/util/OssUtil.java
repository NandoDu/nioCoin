package niocoin.kgsystem.kgbackend.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@SuppressWarnings("SpellCheckingInspection")
@Component
public class OssUtil {

    private final String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private final String accessKeyId = "LTAI5tRq4UBwmMYpw1tBgLPL";
    private final String accessKeySecret = "bT6cfuaH3gu91zxCaxwxW2uG2atuJ9";
    private final OSS client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    public String upload(String bucketName, String key, InputStream data) throws Err {
        try {
            client.putObject(new PutObjectRequest(bucketName, key, data));
        }catch (Exception e){
            e.printStackTrace();
            throw new Err("图片上传失败");
        }
        String url = "https://" + bucketName + "." + endpoint + "/" + key;
        System.out.println(url);
        return url;
    }

}
