package cn.huace.goods.controller.goods;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.common.utils.FileUtil;
import cn.huace.common.utils.excel.ExcelUtil;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.service.GoodsCategoryService;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.bluetooth.entity.BlueTooth;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * Created by yld on 2017/7/20.
 */
@Slf4j
@Api(value = "/admin/category",description= "商品分类管理")
@RestController
@RequestMapping(value = "/admin/category")
public class GoodsCategoryController {

    private static final String GOODSCATEGORY_TEMPLATE_FILE_NAME = "GoodsCategory.xls";
    private static final String GOODSCATEGORY_TEMPLATE_FILE_LOCATION = "static/GoodsCategory.xls";

    @Autowired
    private GoodsCategoryService categoryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "根据超市查询所有商品分类列表")
    @RequestMapping(value = "/tree/list",method = RequestMethod.GET)
    public HttpResult findAllGoodsCategory(String shopId){
        log.info("**** 调用方法：findAllGoodsCategory(),参数={shopId= "+shopId+"}");
        List<GoodsCategory> goodsCategories = null;
        if(StringUtils.isEmpty(shopId)){
            goodsCategories = new ArrayList<>();
        }else {
            goodsCategories = categoryService.findGoodsCategoryByShopId(Integer.parseInt(shopId));
        }
        List<TreeBean> categoryTree = new ArrayList<>();
        for(GoodsCategory category:goodsCategories){
            TreeBean treeBean = new TreeBean();
            if(category != null){
                treeBean.setId(category.getId());
                treeBean.setText(category.getCatName());
                categoryTree.add(treeBean);
            }
        }
        return HttpResult.createSuccess("查询成功！",categoryTree);
    }

    @ApiOperation(value = "保存商品分类")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public HttpResult saveCategory(Integer shopId,String catName,Boolean specialMark){
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("必须选择一个超市！");
        }
        if(StringUtils.isEmpty(catName)){
            return HttpResult.createFAIL("商品分类名称不能为空！");
        }
        //保存商品分类信息
        GoodsCategory category = categoryService.findByCatName(catName,shopId);
        if(category == null){
            category = new GoodsCategory();
            Shop shop = shopService.findOne(shopId);
            category.setCatName(catName);
            category.setShop(shop);
            category.setSpecialMark(specialMark);
            category.setFlag("1");
            GoodsCategory goodsCategory = categoryService.save(category);
            if(goodsCategory == null){
                return HttpResult.createSuccess("添加商品分类失败！");
            }
        }else {
            return HttpResult.createFAIL("该超市商品分类已存在，请勿重复添加！");
        }
        return HttpResult.createSuccess("添加商品分类成功！");
    }
    @ApiOperation(value = "更新商品分类")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResult updateCategory(Integer shopId,GoodsCategory category,Boolean specialMark){
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("必须选择一个超市！");
        }
        if(StringUtils.isEmpty(category.getCatName())){
            return HttpResult.createFAIL("商品分类名称不能为空！");
        }
        //检查商品分类信息是否存在
//        GoodsCategory hasExistCategory = categoryService.findByCatName(category.getCatName(),shopId);
//        if (hasExistCategory != null){
//            return HttpResult.createFAIL("该分类已经存在，修改失败！");
//        }
        GoodsCategory oldCategory = categoryService.findOne(category.getId());
        Shop shop = shopService.findOne(shopId);
        oldCategory.setShop(shop);
        oldCategory.setCatName(category.getCatName());
        oldCategory.setModifiedTime(new Date());
        oldCategory.setSpecialMark(specialMark);

        GoodsCategory newCategory = categoryService.save(oldCategory);
        if(newCategory == null){
            return HttpResult.createFAIL("更新商品分类信息失败！");
        }

        return HttpResult.createSuccess("更新成功！");
    }

    @ApiOperation(value = "查询商品分类详情")
    @RequestMapping(value = "/detail/{catId}",method = RequestMethod.GET)
    public HttpResult findDetail(@PathVariable(name = "catId") Integer catId){
        if(StringUtils.isEmpty(catId)){
            return HttpResult.createFAIL("缺少参数catId");
        }
        GoodsCategory category = categoryService.findOne(catId);
        if(category == null){
            return HttpResult.createFAIL("商品分类信息不存在！");
        }
        return HttpResult.createSuccess("查询成功！",category);
    }

    @ApiOperation(value="批量删除商品分类")
    @RequestMapping(value="/del",method = {RequestMethod.GET})
    public HttpResult delGoodsBatch(String ids,Integer[] shopIds){
        log.info("**** 批量删除商品分类，参数：catIds={},shopIds={}",ids,shopIds);
        if(StringUtils.isEmpty(ids)){
            return HttpResult.createFAIL("请先选择要删除的商品分类信息！");
        }
        String[] idArr = ids.split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for(String id:idArr){
            if(!StringUtils.isEmpty(id)){
                idList.add(Integer.parseInt(id));
            }
        }

        boolean result = categoryService.batchDeleteGoodsCategory(idList);
        if(!result){
            return HttpResult.createFAIL("删除失败！");
        }
        Integer[] catIds = idList.toArray(new Integer[idList.size()]);
        //更新商品所属分类
        List<Goods> goodsList = goodsService.findGoodsByShopAndCategory(catIds,shopIds);
        for(Goods goods:goodsList){
            if(goods != null){
                goods.setCategory(null);
            }
        }
        goodsService.batchUpdateGoods(goodsList);
        return HttpResult.createSuccess("删除成功！");
    }

    @ApiOperation(value = "根据ShopId查询商品分类")
    @RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult findAllGoodsCategoryByShopId(
            @RequestParam(name = "shopId",required = false) Integer shopId,
            @RequestParam(name = "keyword",required = false) String keyword,
            @RequestParam(defaultValue = "1",name = "page")Integer pageNo,
            @RequestParam(defaultValue = "20",name = "rows")Integer pageSize
    ){
        log.info("****** 调用方法：findAllGoodsCategoryByShopId(),参数={ shopId="+shopId+" keyword="+keyword+" pageNo="+pageNo+" pageSize="+pageSize+"}");
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
            searchMap.put("LIKE_catName",keyword);
        }
        searchMap.put("EQ_flag","1");
        Page<GoodsCategory> goodsCategories = categoryService.findAllGoodsCategoryPageable(searchMap,pageNo,pageSize);

        return HttpResult.createSuccess("查询成功！",goodsCategories);
    }

    @ApiOperation(value = "/batch/add",notes = "批量导入商品类型信息")
    @RequestMapping(value = "/batch/add",method = RequestMethod.POST)
    public HttpResult batchAddCategory(@RequestParam(name="excel")MultipartFile file, Integer shopId){
        List<GoodsCategory> categoryList = new ArrayList<>();
        String excelFilePath = FileUtil.saveFile(file);
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            List<Row> rowList = excelUtil.readExcel(excelFilePath);
            if(rowList.size() <= 1){
                //只有标题栏或者什么都没有
                return HttpResult.createFAIL("excel文件没有数据！");
            }
            //删除上传的excel文件
            FileUtil.delFile(excelFilePath);
            for (int i = 1;i<rowList.size();i++){
                GoodsCategory goodsCategory = convertRow2GoodsCategory(rowList.get(i),excelUtil,shopId);
                if (goodsCategory != null){
                    categoryList.add(goodsCategory);
                }
            }
            int result = categoryService.batchInsert(categoryList);
            if(result == categoryList.size()){
                return HttpResult.createSuccess("批量添加商品类型数据成功！");
            }
        } catch (IOException e) {
            log.error("********* 读取excel出错！！！，error = {}",e);
        }
        return HttpResult.createFAIL("批量添加商品类型数据失败！");
    }

    @ApiOperation(value = "/download/category/template",notes = "下载商品分类excel模板文件")
    @RequestMapping(value = "/download/category/template",method = RequestMethod.GET)
    public void downloadGoodsCategoryTemplate(HttpServletResponse response){
        BufferedOutputStream out = null;
        InputStream input = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition","attachment; filename=" + GOODSCATEGORY_TEMPLATE_FILE_NAME);

            input = this.getClass().getClassLoader().getResourceAsStream(GOODSCATEGORY_TEMPLATE_FILE_LOCATION);

            if(input == null){
                out.write("{\"state:\"1,\"msg:下载失败！找不到文件。\"}".getBytes("UTF-8"));
                out.flush();
                return;
            }

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = input.read(buffer)) != -1){
                out.write( buffer, 0, size);
            }

            out.flush();
            log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ " 成功下载文件: "+GOODSCATEGORY_TEMPLATE_FILE_NAME);
        } catch (Exception e) {
            log.error("**** 下载失败，error:"+e.toString(),e);
            try {
                out.write("{\"state:\"1,\"msg:下载失败！\"}".getBytes("UTF-8"));
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            if ( input != null ) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if ( out != null ) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private GoodsCategory convertRow2GoodsCategory(Row row,ExcelUtil excelUtil,Integer shopId){
        if (row == null){
            return null;
        }
        GoodsCategory goodsCategory = new GoodsCategory();

        //商店id
        Shop shop=new Shop();
        shop.setId(shopId);
        goodsCategory.setShop(shop);

        //商品类型
        String catName = excelUtil.getCellValue(row.getCell(0));
        if (!StringUtils.isEmpty(catName)){
                goodsCategory.setCatName(catName.trim());
        }
        //设置标记
        goodsCategory.setFlag("1");
        return goodsCategory;
    }

    public List<Integer> StringToInt(String[] arrs){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<arrs.length;i++){
            list.add(Integer.parseInt(arrs[i]));
        }
        return list;
    }
}
