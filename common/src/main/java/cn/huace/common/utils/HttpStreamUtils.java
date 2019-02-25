package cn.huace.common.utils;

import java.io.*;

/**
 * Created by herry on 2017/5/25.
 */
public class HttpStreamUtils {

    public static void silentClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }catch (IOException e ) {
                //
            }
        }
    }

    /*适合小数据量数据处理*/
    public static String readStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder ret = null;
        try {
            if (is == null) {
                return null;
            }
            char[] buffer = new char[512];
            int count = -1;
            br = new BufferedReader(new InputStreamReader(is));
            ret = new StringBuilder();
            while ((count = br.read(buffer)) != -1) {
                ret.append(buffer,0,count);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            silentClose(is);
            silentClose(br);
        }
        return  ret.toString();
    }
}
