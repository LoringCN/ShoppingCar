package cn.huace.common.utils;

import javax.activation.MimetypesFileTypeMap;


import cn.huace.common.config.SystemConfig;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * This sample demonstrates how to post object under specfied bucket from Aliyun
 * OSS using the OSS SDK for Java.
 * xinyangbucket.oss-cn-shanghai.aliyuncs.com
 */
public class PostObjectToOss {

    public static String PostObject(String folder,String localFilePath) {
        String key = UUID.randomUUID().toString();
        key=folder+"/"+key;
        // 提交表单的URL为bucket域名
        String urlStr = SystemConfig.getInstance().getOssURL();
        // 表单域
        Map<String, String> textMap = new LinkedHashMap<String, String>();
        // key
        textMap.put("key",key);
        // Content-Disposition
        textMap.put("Content-Disposition", "attachment;filename="
                + localFilePath);
        // OSSAccessKeyId
        textMap.put("OSSAccessKeyId", SystemConfig.getInstance().getAccessKeyId());
        // policy
        String policy = "{\"expiration\": \"2120-01-01T12:00:00.000Z\",\"conditions\": [[\"content-length-range\", 0, 104857600]]}";
        String encodePolicy = new String(Base64.encodeBase64(policy.getBytes()));
        textMap.put("policy", encodePolicy);
        // Signature
        String signaturecom = com.aliyun.oss.common.auth.ServiceSignature
                .create().computeSignature( SystemConfig.getInstance().getAccessKeySecret(), encodePolicy);
        textMap.put("Signature", signaturecom);

        Map<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("file", localFilePath);

        boolean result = formUpload(urlStr, textMap, fileMap);
        if(result){
            return key;
        }
        return null;
    }

    private static Boolean formUpload(String urlStr, Map<String, String> textMap,
                                     Map<String, String> fileMap){
        Boolean res = false;
        HttpURLConnection conn = null;
        String boundary = "9431149156168";

        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
//            conn.setRequestProperty("Content-MD5","");//TODO
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Entry<String, String>> iter = textMap.entrySet().iterator();
                int i = 0;

                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();

                    if (inputValue == null) {
                        continue;
                    }

                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }

                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            if (fileMap != null) {
                Iterator<Entry<String, String>> iter = fileMap.entrySet().iterator();

                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();

                    if (inputValue == null) {
                        continue;
                    }

                    File file = new File(inputValue);
                    String filename = file.getName();
                    String contentType = new MimetypesFileTypeMap().getContentType(file);
                    if (contentType == null || contentType.equals("")) {
                        contentType = "application/octet-stream";
                    }

                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(boundary)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    strBuf.append("Content-Type: " + contentType + "\r\n\r\n");

                    out.write(strBuf.toString().getBytes());

                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }

                StringBuffer strBuf = new StringBuffer();
                out.write(strBuf.toString().getBytes());
            }

            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            //根据请求状态判断请求结果,204请求成功，但无数据返回
            int responseCode = conn.getResponseCode();
            if(responseCode == 204 || responseCode == 200){
                res = true;
            }
            // 读取返回数据
//            StringBuffer strBuf = new StringBuffer();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                strBuf.append(line).append("\n");
//            }
//            res = strBuf.toString();
//            reader.close();
//            reader = null;
        } catch (Exception e) {
            System.err.println("Send post request exception: " + e);
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }

        return res;
    }
    public static String  postFile( MultipartFile mfile,String fileFolder){
        if(mfile!=null){
            String fileName=getFileName(mfile.getOriginalFilename());
            File file=new File(SystemConfig.getInstance().getFileTemp()+fileName);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                saveFileFromInputStream(mfile.getInputStream(),file);
                String result=  PostObjectToOss.PostObject(fileFolder,file.getAbsolutePath());
                file.delete();
                return result;
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
    private static void  saveFileFromInputStream(InputStream stream,File file)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        int byteCount = 0;
        byte[] bytes = new byte[1024];
        while ((byteCount = stream.read(bytes)) != -1){
            outputStream.write(bytes, 0, byteCount);
        }
        outputStream.close();
        stream.close();
    }

    public static String getFileName(String imgName){
        //log.info("文件上传:"+imageFileName);
        String newFileName=null;
        long now = System.currentTimeMillis();
        int index = imgName.lastIndexOf(".");
        if (index != -1) {
            newFileName = now + imgName.substring(index);
        } else {
            newFileName = Long.toString(now);
        }
        return newFileName;

    }
}
