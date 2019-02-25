package cn.huace.controller.ad;

import cn.huace.ad.entity.AdGroupV2;
import cn.huace.ad.service.AdGroupV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.controller.base.AdminBasicController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Date:2018/2/28
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/group",description = "v2版本广告组接口")
@RequestMapping(value = "/admin/v2/ad/group")
public class AdGroupV2Controller extends AdminBasicController {

    @Autowired
    private AdGroupV2Service adGroupV2Service;
    @Autowired
    private AdV2Service adV2Service;

    @ApiOperation(value = "查询所有广告组列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public HttpResult listAllAdGroups(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        Page<AdGroupV2> adGroupV2s = adGroupV2Service.listAdGroupV2Pages(new HashMap<>(),page,rows);
        return HttpResult.createSuccess("查询成功!",adGroupV2s);
    }

    @ApiOperation(value = "搜索广告组")
    @RequestMapping(value = "/list/search",method = RequestMethod.POST)
    public HttpResult searchAdGroup(
            @RequestParam(name = "groupName") String groupName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        if (StringUtils.isEmpty(groupName)) {
            return HttpResult.createSuccess("请输入要搜索的广告组名称！");
        }
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("LIKE_adGroupName",groupName);

        Page<AdGroupV2> adGroupV2s = adGroupV2Service.listAdGroupV2Pages(searchMap,page,rows);
        return HttpResult.createSuccess("查询成功!",adGroupV2s);
    }

    @ApiOperation(value = "查询广告组树")
    @RequestMapping(value = "/tree",method = RequestMethod.POST)
    public HttpResult tree(){
        List<TreeBean> treeBeans = new ArrayList<>();
        List<AdGroupV2> adGroupV2List = adGroupV2Service.findAll();
        if (!CollectionUtils.isEmpty(adGroupV2List)) {
            for (AdGroupV2 group:adGroupV2List) {
                TreeBean tree = new TreeBean();
                tree.setId(group.getId());
                tree.setText(group.getAdGroupName());
                treeBeans.add(tree);
            }
        }
        return HttpResult.createSuccess("查询成功！",treeBeans);
    }

    @ApiOperation(value = "新增、修改广告组")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public HttpResult save(AdGroupV2 adGroup){
        if (adGroup.isNew()) {
            String groupName = adGroup.getAdGroupName();
            if (StringUtils.isEmpty(groupName)) {
                return HttpResult.createFAIL("广告分组名不能为空！");
            }
            // 检查要添加的广告分组名是否存在
            Boolean existGroupName = adGroupV2Service.existAdGroupName(adGroup.getAdGroupName());
            if (existGroupName) {
                return HttpResult.createFAIL("广告分组名已存在，请修改为名称！");
            }
            AdGroupV2 result = adGroupV2Service.save(adGroup);
            if (result != null) {
                return HttpResult.createSuccess("新增成功！");
            }
            return HttpResult.createFAIL("新增失败！");
        } else {
            AdGroupV2 oldGroup = adGroupV2Service.findOne(adGroup.getId());
            String groupName = adGroup.getAdGroupName();
            if (StringUtils.isEmpty(groupName)) {
                return HttpResult.createFAIL("广告分组名不能为空！");
            }

            oldGroup.setAdGroupName(adGroup.getAdGroupName());
            oldGroup.setRemark(adGroup.getRemark());

            AdGroupV2 result = adGroupV2Service.save(oldGroup);
            if (result != null) {
                return HttpResult.createSuccess("修改成功！");
            }
            return HttpResult.createFAIL("修改失败！");
        }
    }

    @ApiOperation(value = "删除广告组")
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.GET)
    public HttpResult delete(@PathVariable("id") Integer groupId){
        // 检查广告组是否被广告使用
        boolean isAvailable = adV2Service.isAvailableGroup(groupId);
        if (isAvailable) {
            adGroupV2Service.delete(groupId);
            return HttpResult.createSuccess("删除成功！");
        }
        return HttpResult.createFAIL("该广告组已被使用，不能删除！");
    }

    @ApiOperation(value = "删除广告组")
    @RequestMapping(value = "/find/{id}",method = RequestMethod.GET)
    public HttpResult findOne(@PathVariable("id") Integer groupId){
        return HttpResult.createSuccess("查询成功！",adGroupV2Service.findOne(groupId));
    }

}
