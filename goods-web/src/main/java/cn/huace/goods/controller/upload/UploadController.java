package cn.huace.goods.controller.upload;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.PostObjectToOss;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *  商品图片、excel文件上传和下载入口
 * Created by yld on 2017/5/22.
 */
@Slf4j
@RestController
@Api(value = "/admin/product",description = "商品管理")
@RequestMapping("/admin/product")
public class UploadController {

    /** 商品模板文件名 */
    private static final String GOODS_TEMPLATE_FILE_NAME = "goods_template.xls";
    /** 商品模板文件位置 */
    private static final String GOODS_TEMPLATE_FILE_LOCATION = "static/goods_template.xls";

    /** 商品图片定位信息模板文件名 */
    private static final String GOODS_PICTRUE_LOCATION_FILE_NAME = "goods_pictrue_location.xls";
    /**
     * 商品图片定位信息模板下载地址
     */
    private static final String GOODS_PICTRUE_LOCATION = "static/goods_pictrue_location.xls";

    @ApiOperation(value = "上传单张商品图片")
    @RequestMapping(value="/upload/img",method = RequestMethod.POST)
    public HttpResult uploadSingleImg(@RequestParam(name="goodsImg",required = false) MultipartFile goodsImg){
        log.info("***　调用方法uploadSingleImg(),单文件上传开始．．．");
        String imgUrl = PostObjectToOss.postFile(goodsImg, Contants.GOODS_IMG_TEMP_FOLDER);

        return HttpResult.createSuccess("上传成功！",imgUrl);
    }


    @ApiOperation(value = "下载商品excel数据模板")
    @RequestMapping(value = "/download/goodsTemplate",method = RequestMethod.GET)
    public void downloadGoodsExcelTemplate(HttpServletResponse response){
        BufferedOutputStream out = null;
        InputStream input = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition","attachment; filename=" + GOODS_TEMPLATE_FILE_NAME);

            input = this.getClass().getClassLoader().getResourceAsStream(GOODS_TEMPLATE_FILE_LOCATION);

            if(input == null){
                out.write("{\"state:\"1,\"msg:下载失败！找不到文件。\"}".getBytes("UTF-8"));
                out.flush();
                return;
            }

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = input.read(buffer)) != -1)   {
                out.write( buffer, 0, size);
            }

            out.flush();
            log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ " 成功下载文件: "+GOODS_TEMPLATE_FILE_NAME);
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

    @ApiOperation(value = "下载商品图片位置信息excel数据模板")
    @RequestMapping(value = "/download/pictrueMode",method = RequestMethod.GET)
    public void downGoodsPictrueMode(HttpServletResponse response){
        BufferedOutputStream out = null;
        InputStream input = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition","attachment; filename=" + GOODS_PICTRUE_LOCATION_FILE_NAME);

            input = this.getClass().getClassLoader().getResourceAsStream(GOODS_PICTRUE_LOCATION);

            if(input == null){
                out.write("{\"state:\"1,\"msg:下载失败！找不到文件。\"}".getBytes("UTF-8"));
                out.flush();
                return;
            }

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = input.read(buffer)) != -1)   {
                out.write( buffer, 0, size);
            }

            out.flush();
            log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ " 成功下载文件: "+GOODS_PICTRUE_LOCATION_FILE_NAME);
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

}
