package cn.huace.common.utils;

import cn.huace.common.config.SystemConfig;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  文件工具类
 * Created by yld on 2017/5/23.
 */
public class FileUtil {

    /**
     * 保存文件，返回文件绝对路径
     * @param mfile
     * @return
     */
    public static String saveFile(MultipartFile mfile){
        if(mfile == null){
            return null;
        }
        String fileName=getFileName(mfile.getOriginalFilename());
        File file=new File(SystemConfig.getInstance().getFileTemp()+fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
            saveFileFromInputStream(mfile.getInputStream(),file);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
    public static void delFile(String absfilePath){
        if(StringUtils.isEmpty(absfilePath)){
            return;
        }
        File file = new File(absfilePath);
        if(file.exists()){
            file.delete();
        }
    }
    private static void  saveFileFromInputStream(InputStream stream, File file)
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
}
