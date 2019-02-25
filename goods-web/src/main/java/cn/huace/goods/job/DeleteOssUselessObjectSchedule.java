package cn.huace.goods.job;

import cn.huace.common.config.SystemConfig;
import cn.huace.common.utils.Contants;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定时删除无用商品图片
 * Created by yld on 2017/12/8.
 */
@Configuration
@EnableScheduling
@Slf4j
public class DeleteOssUselessObjectSchedule {

    @Autowired
    private GoodsService goodsService;
    @Resource(name = "systemConfig")
    private SystemConfig cfg;

    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bulketName;
    //单次请求可取回最大文件数量
    private static final int PER_MAX_KEYS = 1000;

    @PostConstruct
    public void init(){
        endpoint = cfg.getOssEndpoint();
        accessKeyId = cfg.getAccessKeyId();
        accessKeySecret = cfg.getAccessKeySecret();
        bulketName = cfg.getOssBulketName();
    }

//    @Scheduled(fixedRate = 24 * 60 * 60 *1000)
    public void cleanUselessGoodsImg(){
        log.info("***** 开始清理无用商品图片~~~~");
        OSSClient client = getOSSClient();
        List<String> ossImgKeysList = getAllObjectKeys(client);
        Set<String> usableOssImgKeysSet = new HashSet<>();

        //查询所有正在使用的key
        List<Goods> goodsList = goodsService.findAllGoodsImgUrls();
        for(Goods goods:goodsList){
            if(goods != null){
                String coverImgUrl = goods.getCoverImgUrl();
                String detailImgUrl = goods.getDetailImgUrl();
                addUsableOssImgToSet(coverImgUrl,usableOssImgKeysSet);
                addUsableOssImgToSet(detailImgUrl,usableOssImgKeysSet);
            }
        }
        if(ossImgKeysList.containsAll(usableOssImgKeysSet)){
            log.info("********* OSS没有需要清理的无用图片！！");
            return;
        }
        //无用图片
        ossImgKeysList.removeAll(usableOssImgKeysSet);
        if(!CollectionUtils.isEmpty(ossImgKeysList)){
            List<String> delResult = deleteUselessImgsBatch(client,ossImgKeysList);
            log.info("****** 删除无用图片数量：{}，返回结果：{}",delResult.size(),delResult);
        }

    }
    private OSSClient getOSSClient(){
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 取回指定bulketName下指定前缀目录下所有objects的key列表
     */
    private List<String> getAllObjectKeys(OSSClient client){
        List<String> objectImgKeys =  new ArrayList<>();
        ObjectListing objectListing = null;
        //分页取回所有存在图片对象
        String nextMarker = "";
        do{
            objectListing = client.listObjects(
                    new ListObjectsRequest(bulketName).withPrefix(Contants.GOODS_IMG_TEMP_FOLDER)
                            .withMarker(nextMarker).withMaxKeys(PER_MAX_KEYS)
            );
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary sum : sums) {
                objectImgKeys.add(sum.getKey());
            }
            nextMarker = objectListing.getNextMarker();
        }while (objectListing.isTruncated());

        return objectImgKeys;
    }

    /**
     * 批量删除无用图片
     */
    private List<String> deleteUselessImgsBatch(OSSClient client,List<String> uselessOssImgKeysList){
        DeleteObjectsResult result = client.deleteObjects(
                new DeleteObjectsRequest(bulketName).withKeys(uselessOssImgKeysList));
        return result.getDeletedObjects();
    }
    /**
     * 找出数据库中有用的OSS图片链接，并存入set集合中
     */
    private void addUsableOssImgToSet(String ossImgUrl,Set<String> usableOssImgKeysSet){
        if(ossImgUrl != null && ossImgUrl.startsWith(Contants.GOODS_IMG_TEMP_FOLDER)){
            usableOssImgKeysSet.add(ossImgUrl);
        }
    }
}
