package cn.huace.statis.core;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.huace.statis.handle.BasicHandle;
import cn.huace.statis.handle.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 
 * 
 * 
 *
 * 
 */
@Slf4j
public class StatReadLogTask implements Runnable {

    private String logPath;
    private FileReader fileReader = null;
    private BufferedReader bufferedFileReader = null;
    private String day=null;
    // private String date;
    public StatReadLogTask(String path,String day){
        this.logPath=path;
        this.day=day;
    }
    
    @Override
    public int hashCode() {
        return (logPath).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatReadLogTask other = (StatReadLogTask) obj;
        if (!other.getLogPath().equals(logPath))
            return false;
        return true;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void run() {
        try {
            fileReader = new FileReader(logPath);
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException:" + e);
        }
        bufferedFileReader = new BufferedReader(fileReader);
        String line = null;
        String spaceDelimiter = " ";
        int lineNO = 0;
        try {
            while ((line = bufferedFileReader.readLine()) != null) {
                lineNO++;
                String[] arr = StringUtils.split(line, spaceDelimiter);
                if (arr != null && arr.length >= 7) {
                    if (arr[4].equals("STATIS")) {
                        String timeStr=arr[0];
                        if (!timeStr.contains(day)) {
                            log.info("It's not yesterday,line:"
                                    + lineNO+" day:"+timeStr);
                            continue;
                        }
                        String clientType = arr[5];// clientType:ucenter
                        BasicHandle parser = (BasicHandle) ParserBeanFactory
                                .getParserBean(clientType);
                        if (parser != null) {
                            try {
                                String useLog=line.substring(line.indexOf(clientType+" ")+clientType.length()+1);
                                Map<String,String> keyValue=parseLog(useLog);
                                parser.readLog(keyValue, timeStr);
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("IOException:" + e);
        }
    }


    private Map<String,String> parseLog(String useLog){
        Map<String,String> keyValue=new HashMap<String,String>();
        String[] arr = useLog.split("-");
            for(String ar:arr){
                String[] keyValueStr=ar.split(":");
                if(keyValueStr.length>1){
                    keyValue.put(keyValueStr[0],keyValueStr[1]);
                }else{
                    keyValue.put(keyValueStr[0],"");
                }

            }
        return keyValue;
    }
}
