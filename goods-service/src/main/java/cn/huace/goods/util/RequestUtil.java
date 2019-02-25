package cn.huace.goods.util;

import cn.huace.common.config.SystemConfig;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.PostObjectToOss;
import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Http请求工具类
 * Created by yld on 2017/8/21.
 */
public class RequestUtil {
    private static final String REQUEST_METHOD_GET = "GET";
    private static final String CHARSET = "UTF-8";
    /**
     *  发送httpGet请求，返回JSON对象数据
     * @param targetUrl 目标url
     * @return
     * @throws IOException
     */
    public static JSONObject sendHttpGet(String targetUrl,Map<String,Object> params) throws IOException {
        JSONObject result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        //发送请求
        try {
            //构建请求参数
            StringBuilder builder = new StringBuilder();
            builder.append(targetUrl+"?");
            if(params != null){
                for(Map.Entry<String,Object> entry:params.entrySet()){
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    builder.append(key + "=" +URLEncoder.encode(value,CHARSET) +"&");
                }
            }
            String url = builder.toString().substring(0,builder.toString().length()-1);
            System.out.println("***** GET请求URL："+ url);
            HttpGet httpGet = new HttpGet(url);

            response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //获取结果
                String content = EntityUtils.toString(response.getEntity());
                if(!StringUtils.isEmpty(content)){
                    result = JSONObject.fromObject(content);
                }
            }
            return result;
        }finally {
            //释放资源
            if(client != null){
                client.close();
            }
            if(response != null){
                response.close();
            }
        }
    }
    /**
     * 发送httpPost请求，返回JSON对象数据
     * @param targetUrl 目标url
     * @param params 请求参数
     * @return
     * @throws IOException
     */
    public static JSONObject sendHttpPost(String targetUrl,Map<String,Object> params) throws IOException {
        JSONObject result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(targetUrl);
            //构建请求参数
            List<NameValuePair> list = new ArrayList<>();
            if(params != null){
                for(Map.Entry<String,Object> entry:params.entrySet()){
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    NameValuePair pair = new BasicNameValuePair(key,value);
                    list.add(pair);
                }
            }

            if(!CollectionUtils.isEmpty(list)){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,CHARSET);
                httpPost.setEntity(entity);
            }
            response = client.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String content = EntityUtils.toString(response.getEntity());
                if(!StringUtils.isEmpty(content)){
                    result = JSONObject.fromObject(content);
                }
            }
            return result;
        }finally {
            //释放资源
            if(client != null){
                client.close();
            }
            if(response != null){
                response.close();
            }
        }
    }

    /**
     *  用于保存抓取到的图片
     * @param linkImgUrl  图片链接地址
     * @param fileDir     保存目录
     * @return			   返回布尔值以确定保存是否成功
     */
    public static String saveImg(String linkImgUrl,String fileDir) throws IOException {
        //上传后文件名
        String newImgName = null;
        //创建Url
        URL url = new URL(linkImgUrl);
        //获取链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式
        conn.setRequestMethod(REQUEST_METHOD_GET);
        //设置读取超时时间
        conn.setReadTimeout(6*10000);

        if(conn.getResponseCode() == 200){
            //获取输入流
            InputStream in = conn.getInputStream();
            //读取链接中数据
            byte[] imgDatas = readStream(in);
            if(imgDatas.length > 1024){//图片大于一个字节才保存
                String localDir = SystemConfig.getInstance().getFileTemp();
                if(!localDir.endsWith("/")){
                    localDir += "/";
                }
                //重命名图片
                String imgName = UUID.randomUUID().toString() + System.currentTimeMillis()+ ".png";
                //创建保存图片flie
                File file = new File(localDir+imgName);
                if(!file.getParentFile().exists()){
                    //不存在则创建file
                    file.getParentFile().mkdirs();
                }

                //创建文件输出流，写出图片
                FileOutputStream fos
                        = new FileOutputStream(file);
                fos.write(imgDatas);
                //释放资源
                fos.close();
                in.close();

                //上传图片至OOS
                newImgName = PostObjectToOss.PostObject(fileDir,file.getAbsolutePath());
                //删除临时文件
                file.delete();
            }
        }
        return newImgName;
    }
    /**
     * 读取图片链接地址url中数据，以字节数组形式返回
     * @param   in    用于读取数据的输入流
     * @return        返回字节数组
     */
    public static byte[] readStream(InputStream in) throws IOException{
        //创建字节输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //创建字节数组
        byte[] buffer = new byte[1024];
        //实际读取到的字节数
        int len = -1;
        while((len=in.read(buffer))!= -1){
            //将字节写入字节数组中
            bos.write(buffer,0,len);
        }
        //关闭流
        bos.close();
        in.close();
        return bos.toByteArray();
    }


    public static void main(String[] args) throws Exception {
        String imgUrl = "http://shoppingcar-storage.oss-cn-hangzhou.aliyuncs.com/test/974b0d02-a01d-4aae-8df8-743241638f88";
//        String imgUrl = "http://localhost:8084/api/1.jpg";
//        saveImg(imgUrl,"test");
        Map<String,Object> params = new HashMap<>();
        params.put("timestamp",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("pageNo",1);
        params.put("pageSize",20);
        sendHttpGet("http://localhost:8084/api/product/test/add",params);
    }
}
