package cn.huace.goods.controller.goods;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.OssDeletionUtils;
import cn.huace.common.utils.PostObjectToOss;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.Theme;
import cn.huace.goods.entity.ThemeType;
import cn.huace.goods.service.ThemeService;
import cn.huace.goods.service.ThemeTypeService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by yld on 2017/7/24.
 */
@Slf4j
@RestController
@Api(value = "/admin/theme",description = "主题商品入口")
@RequestMapping(value = "/admin/theme")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ThemeTypeService themeTypeService;

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "查询主题分类")
    @RequestMapping(value = "/type/tree",method = {RequestMethod.GET})
    public HttpResult findThemeTypeTree(){
        List<ThemeType> themeTypes = themeTypeService.findAllThemeTypeAvailable();
        List<TreeBean> treeBeans = new ArrayList<>();
        if(!CollectionUtils.isEmpty(themeTypes)){
            for(ThemeType themeType:themeTypes){
                if(themeType != null){
                    TreeBean treeBean = new TreeBean();
                    treeBean.setId(themeType.getId());
                    treeBean.setText(themeType.getName());
                    treeBeans.add(treeBean);
                }
            }
        }
        return HttpResult.createSuccess("查询成功！",treeBeans);
    }
    @ApiOperation(value = "根据Id查询主题详情")
    @RequestMapping(value = "/detail/{id}",method = {RequestMethod.GET})
    public HttpResult findThemeById(@PathVariable("id") Integer id){
        if(StringUtils.isEmpty(id)){
            return HttpResult.createFAIL("主题Id不能为空!");
        }
        Theme theme = themeService.findOne(id);
        return HttpResult.createSuccess("查询成功！",theme);
    }
    @ApiOperation(value = "查询主题列表")
    @RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult findAllThemes(
            @RequestParam(name = "shopId",required = false) Integer shopId,
            @RequestParam(name = "keyword",required = false) String keyword,
            @RequestParam(name = "page",defaultValue = "1") Integer pageNo,
            @RequestParam(name = "rows",defaultValue = "20") Integer pageSize
    ){
        log.info("*** 查询主题列表，参数：{shopId= "+shopId+" keyword= "+keyword+" pageNo= "+pageNo+" pageSize= "+pageSize+"}");
        Map<String,Object> searchMap = new HashMap<>();
//        if(!StringUtils.isEmpty(shopId)){
//            searchMap.put("EQ_shop.id",shopId);
//        }
        if(StringUtils.isEmpty(shopId)){
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = (SystemUser) redisService.getObjectCacheValue("cn.huace.sys.systemUser:"+user.getAccount(),3);
            if(systemUser.getType() != 0 ){
                String shopIds = systemUser.getShopIds();
                String [] shops = shopIds.split(",");
                if(shopIds.split(",").length == 1){
                    searchMap.put("EQ_shop.id",Integer.parseInt(shopIds.trim()));
                }else{
                    searchMap.put("INI_shop.id",StringToInt(shops));
                }
            }
        }else{
            searchMap.put("EQ_shop.id",shopId);
        }
        if(!StringUtils.isEmpty(keyword)){
            searchMap.put("LIKE_name",keyword);
        }
        searchMap.put("EQ_flag","1");
        Page<Theme>  themes = themeService.findAllThemes(searchMap,pageNo,pageSize);
        return HttpResult.createSuccess("查询成功！",themes);
    }
    @ApiOperation(value = "添加主题")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public HttpResult addThemes(
            @RequestParam(name = "coverImgFile") MultipartFile coverImgFile,
            @RequestParam(name = "shopId") Integer shopId,
            @RequestParam(name = "name") String themeName,
            @RequestParam(name = "themeType") Integer themeTypeId,
            @RequestParam(name = "tag",required = false) String ids,
            @RequestParam(name = "remark",required = false) String remark
    ){
        log.info("**** 添加主题，参数：method={shopId= "+shopId+" shopId= "+themeName+" shopId= "+ids+" imgName="+ coverImgFile.getOriginalFilename()+"}");
        if(StringUtils.isEmpty(themeName)){
            return HttpResult.createFAIL("主题名不能为空！");
        }
        if(coverImgFile.getSize() == 0){
            return HttpResult.createFAIL("主题封面图不能为空！");
        }
        if(StringUtils.isEmpty(themeTypeId)){
            return HttpResult.createFAIL("主题分类不能为空！");
        }
        //检查要添加的主题是否存在
        Boolean checkResult = themeService.checkThemeByShopIdAndThemeType(shopId,themeTypeId,themeName);
        if(checkResult){
            return HttpResult.createFAIL("该超市该主题类型已存在，请勿重复添加！");
        }
        //上传主题封面图至图片服务器
        String imgUrl = PostObjectToOss.postFile(coverImgFile, Contants.THEME_COVER_IMG_FOLDER);
        if(StringUtils.isEmpty(imgUrl)){
            return HttpResult.createFAIL("上传图片失败，请重试！");
        }
        Shop shop = shopService.findOne(shopId);
        ThemeType themeType = themeTypeService.findOne(themeTypeId);

        Theme theme = new Theme();
        theme.setName(themeName);
        theme.setCoverImg(imgUrl);
        theme.setTag(ids);
        theme.setShop(shop);
        theme.setType(themeType);
        theme.setFlag("1");
        theme.setActive("-1");//默认不生效

        if(!StringUtils.isEmpty(remark)){
            theme.setRemark(remark);
        }
        Theme result = themeService.save(theme);
        if(result == null){
            return HttpResult.createFAIL("添加失败！");
        }
        return HttpResult.createSuccess("添加成功！");
    }
    @ApiOperation(value = "修改主题")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResult editTheme(
            Theme theme,Integer shopId,
            @RequestParam(name = "themeType")Integer themeTypeId,
            @RequestParam(name = "coverImgFile") MultipartFile coverImgFile
    ){
        log.info("**** 调用方法修改主题，参数：{shopId= "+shopId+" theme= "+theme+" imgName="+ coverImgFile.getOriginalFilename()+"}");
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("参数shopId不能为空！");
        }
        if(StringUtils.isEmpty(theme.getName())){
            return HttpResult.createFAIL("主题名称不能为空！");
        }
        if(StringUtils.isEmpty(themeTypeId)){
            return HttpResult.createFAIL("主题分类不能为空！");
        }
        Theme oldTheme = themeService.findOne(theme.getId());
        if(oldTheme == null){
            return HttpResult.createFAIL("主题不存在或已被删除！");
        }
        String coverImgUrl = "";
        String oldImgUrl = oldTheme.getCoverImg();
        if(coverImgFile.getSize() != 0){
            //上传主题封面图至图片服务器
            String imgUrl = PostObjectToOss.postFile(coverImgFile, Contants.THEME_COVER_IMG_FOLDER);
            if(!StringUtils.isEmpty(imgUrl)){
                //修改为新图片
                coverImgUrl = imgUrl;
                try {
                    if(!StringUtils.isEmpty(oldImgUrl)){
                        //删除oss服务器上老图片
                        Boolean delResult = OssDeletionUtils.delete(oldImgUrl);
                        if(!delResult){
                            log.info("**** 删除主题旧封面图片失败！coverImg= "+oldImgUrl);
                        }
                    }
                }catch (Exception e){
                    log.error("********** 删除旧图片失败！",e);
                }
            }else {
                return HttpResult.createFAIL("上传图片失败，请重试！");
            }
        }else {
            coverImgUrl = oldImgUrl;
        }

        if(shopId != oldTheme.getShop().getId()){
            Shop shop = shopService.findOne(shopId);
            if(shop == null){
                return HttpResult.createFAIL("超市不存在或已被删除！");
            }
            oldTheme.setShop(shop);
        }

        if(themeTypeId != oldTheme.getType().getId()){
            ThemeType themeType = themeTypeService.findOne(themeTypeId);
            oldTheme.setType(themeType);
        }
        oldTheme.setCoverImg(coverImgUrl);
        oldTheme.setName(theme.getName());
        oldTheme.setTag(theme.getTag());
        oldTheme.setModifiedTime(new Date());
        Theme result = themeService.save(oldTheme);
        if(result == null){
            return HttpResult.createFAIL("修改主题失败！");
        }
        return HttpResult.createSuccess("修改主题成功！");
    }

    @ApiOperation(value = "删除主题")
    @RequestMapping(value = "/del",method = RequestMethod.GET)
    public HttpResult delTheme(Integer[] ids){
        log.info("***** 删除主题，参数：ids ={}",Arrays.asList(ids));
        if(ids.length == 0 ){
            return HttpResult.createFAIL("请选择要删除的主题！");
        }
        boolean result = themeService.delTheme(Arrays.asList(ids));
        if(!result){
            return HttpResult.createFAIL("删除主题失败！");
        }
        return HttpResult.createSuccess("删除主题成功！");
    }
    @ApiOperation(value = "上架主题")
    @RequestMapping(value = "/online/{id}",method = RequestMethod.GET)
    public HttpResult onlineTheme(@PathVariable("id") Integer id){
        log.info("***** 上架主题，参数：id ={}",id);
        Theme theme = themeService.findOne(id);
        //检查是否已有同主题类型主题处于上架状态
        Integer shopId = theme.getShop().getId();
        Integer themeTypeId = theme.getType().getId();
        Theme hasOnlineTheme = themeService.checkIsExistOnlineTheme(shopId,themeTypeId);
        if(hasOnlineTheme != null){
            String shopName = hasOnlineTheme.getShop().getName();
            String themeTypeName = hasOnlineTheme.getType().getName();
            String themeName = hasOnlineTheme.getName();
            //已有同类型主题上架，必须先下架该主题
            return HttpResult.createFAIL("超市<<"+shopName+">> 已存在《"+themeTypeName+"》类型上架主题<"+themeName+">，请先下架该主题！");
        }
        theme.setActive("1");
        Theme result = themeService.save(theme);
        if(result == null){
            return HttpResult.createFAIL("上架失败！");
        }
        return HttpResult.createSuccess("上架成功！");
    }
    @ApiOperation(value = "下架主题")
    @RequestMapping(value = "/offline/{id}",method = RequestMethod.GET)
    public HttpResult offlineTheme(@PathVariable("id") Integer id){
        log.info("***** 下架主题，参数：id ={}",id);
        Theme theme = themeService.findOne(id);
        theme.setActive("-1");
        Theme result = themeService.save(theme);
        if(result == null){
            return HttpResult.createFAIL("下架失败！");
        }
        return HttpResult.createSuccess("下架成功！");
    }

    public List<Integer> StringToInt(String[] arrs){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<arrs.length;i++){
            list.add(Integer.parseInt(arrs[i]));
        }
        return list;
    }
}
