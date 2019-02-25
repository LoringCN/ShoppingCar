package cn.huace.controller.ad;

import cn.huace.ad.entity.AdTypeV2;
import cn.huace.ad.service.AdTypeV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Date:2018/3/9
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/type",description = "v2版本广告类型")
@RequestMapping(value = "/admin/v2/ad/type")
public class AdTypeV2Controller extends AdminBasicController{
    @Autowired
    private AdTypeV2Service adTypeV2Service;

    @ApiOperation(value = "查询所有广告类型列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public HttpResult listAllAdType(){
        log.info("**** 查询所有广告类型列表：listAllAdType()");
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_flag",true);
        List<AdTypeV2> adTypeList = adTypeV2Service.findAll(searchMap);
        return HttpResult.createSuccess("查询成功！",sortedList(adTypeList));
    }

    @ApiOperation(value = "根据Id查询广告类型详情")
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public HttpResult detail(@PathVariable("id")Integer id){
        return HttpResult.createSuccess("查询详情成功！",adTypeV2Service.findOne(id));
    }

    private Object sortedList(List<AdTypeV2> adTypeList) {
        adTypeList.sort((AdTypeV2 o1,AdTypeV2 o2) -> {
            int o1TypeCode = o1.getCode();
            int o2TypeCode = o2.getCode();
            if (o1TypeCode == AdCodeConstants.AdV2Type.TYPE_CODE_VIDEO
                && o2TypeCode != AdCodeConstants.AdV2Type.TYPE_CODE_VIDEO) {
                return -1;
            }
            if (o1TypeCode != AdCodeConstants.AdV2Type.TYPE_CODE_VIDEO
                    && o2TypeCode == AdCodeConstants.AdV2Type.TYPE_CODE_VIDEO) {
                return 1;
            }
            if (o1TypeCode > o2TypeCode) {
                return 1;
            }
            if (o1TypeCode < o2TypeCode) {
                return  -1;
            }
            return 0;
        });
        return adTypeList;
    }
}
