package cn.huace.controller.ad;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.PostObjectToOss;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 广告资源上传
 * Date:2018/1/31
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/upload",description = "广告资源上传接口")
@RequestMapping(value = "/admin/v2/ad/upload")
public class UploadController {

    @ApiOperation(value = "上传广告资源")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResult upload(@RequestParam(name = "file") MultipartFile adFile){
        log.info("***** 开始上传广告资源文件！");
        if(adFile == null || adFile.isEmpty()){
            return HttpResult.createFAIL("广告资源文件不能为空！");
        }

        String md5 = null;
        String url = PostObjectToOss.postFile(adFile, Contants.IMG_FOLDER_AD);
        if(StringUtils.isEmpty(url)){
           return HttpResult.createFAIL("上传失败!");
        }
        log.info("***** 上传成功！url = {}",url);
        try {
            md5 = DigestUtils.md5DigestAsHex(adFile.getInputStream());
        } catch (IOException e) {
            log.error("计算广告文件md5值出错！");
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("url",url);
        resultMap.put("md5",md5);
        return HttpResult.createSuccess("上传成功！",resultMap);
    }
}
