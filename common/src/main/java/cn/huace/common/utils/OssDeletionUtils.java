package cn.huace.common.utils;

import cn.huace.common.config.SystemConfig;
import com.aliyun.oss.OSSClient;

public class OssDeletionUtils {

    /**
     * 删除OSS上指定的文件
     *
     * @param fileKey 文件名
     * @return
     */
    public static boolean delete(String fileKey) {
        OSSClient client = null;
        try {
            SystemConfig cfg = SystemConfig.getInstance();
            String endpoint = cfg.getOssEndpoint();
            String accessKeyId = cfg.getAccessKeyId();
            String accessKeySecret = cfg.getAccessKeySecret();
            String bulketName = cfg.getOssBulketName();
            client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            client.deleteObject(bulketName, fileKey);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(client != null){
                client.shutdown();
            }
        }
        return false;
    }
}
