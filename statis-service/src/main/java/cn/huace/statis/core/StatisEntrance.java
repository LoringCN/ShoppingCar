package cn.huace.statis.core;

import cn.huace.statis.handle.BasicHandle;
import cn.huace.statis.handle.Constants;
import cn.huace.statis.handle.HandleData;
import cn.huace.statis.mongo.service.StatisticMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class StatisEntrance {

    @Autowired
    private StatisticMongoService statisticMongoService;

    public void doStatis(String yesterday, String basePath) throws IOException {
        readLog(yesterday, basePath);
        //日统计
        handdleDayData(yesterday);
    }

    public void readLog(String yesterday, String basePath) throws IOException {
        log.info("-------read log start！--------------------------------------");
        HandleData.freshData();
        List<StatReadLogTask> readLoglist = new ArrayList<StatReadLogTask>();
        String logPath = basePath + "/" + yesterday;
//        String logPath = basePath + "/" ;
        File logFile = new File(logPath);
        log.info("logPath:" + logPath);
        if (logFile.isDirectory()) {
            for (File log : logFile.listFiles()) {
                String fileName = log.getName();
//                if(fileName.contains(yesterday)){
                StatReadLogTask stb = new StatReadLogTask(log.getAbsolutePath(), yesterday);
                readLoglist.add(stb);
//                }
            }
        }
        log.info("read_Log_task:" + readLoglist.size());
        if (readLoglist != null && readLoglist.size() > 0) {
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(readLoglist.size());
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 7, 5, TimeUnit.MINUTES, queue,
                    new ThreadPoolExecutor.CallerRunsPolicy());

            for (StatReadLogTask task : readLoglist) {
                executor.execute(task);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                log.info("readLogTasks total:{}, completed:{}", executor.getTaskCount(), executor.getCompletedTaskCount());
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    log.info("e:" + e.getMessage());
                }
            }
        }
        log.info("-------update log end！--------------------------------------");
    }

    public void handdleDayData(String yesterday) {
        log.info("-------handdleDayData log start！--------------------------------------");
        List<StatHandTask> updatetask = new ArrayList<StatHandTask>();
        Set<String> keys = Constants.PARSERS_Map.keySet();
        for (String str : keys) {
            BasicHandle hanndle = (BasicHandle) ParserBeanFactory.getParserBean(str);
            updatetask.add(new StatHandTask(hanndle, yesterday));
        }
        log.info("update_stat_task:" + updatetask.size());
        if (updatetask.size() > 0) {
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(updatetask.size());
            ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 7, 5, TimeUnit.MINUTES, queue,
                    new ThreadPoolExecutor.CallerRunsPolicy());
            for (StatHandTask task : updatetask) {
                executor.execute(task);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                log.info("handleLogTask: total:{}, completed:{}", executor.getTaskCount(), executor.getCompletedTaskCount());
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    log.info("e:" + e.getMessage());
                }
            }
            HandleData.freshData();
        }

        log.info("-------handdleDayData log end！--------------------------------------");
    }


}
