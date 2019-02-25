package cn.huace.statis.core;
import cn.huace.statis.handle.BasicHandle;
import lombok.extern.slf4j.Slf4j;
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
public class StatHandTask implements Runnable {
    private BasicHandle handle=null;
    private String time;
    // private String date;
    public StatHandTask(BasicHandle handle, String time){
        this.handle=handle;
        this.time=time;
    }
    
    @Override
    public int hashCode() {
        return (handle).hashCode();
    }

    public void run() {
        try {
         if(handle!=null){
             handle.handleData(time); 
         }
        } catch (Exception e) {
            log.error("IOException:" + e);
        }
    }

}
