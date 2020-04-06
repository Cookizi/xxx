//package top.cookizi.xxx.service;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Service;
//import top.cookizi.xxx.bean.Task;
//import top.cookizi.xxx.mapper.TaskMapper;
//import top.cookizi.xxx.service.downloader.Downloader;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class TaskRunner implements ApplicationRunner {
//    @Autowired
//    private TaskMapper taskMapper;
//    @Autowired
//    @Qualifier("downloaderMapper")
//    private Map<String, Downloader> downloaderMap;
//
//
//    @Override
//    public void run(ApplicationArguments args) throws InterruptedException {
//        log.info("开始载入任务");
//        List<Task> tasks = taskMapper.selectList(new QueryWrapper<Task>().eq("status", 0));
//        if (tasks.size() == 0) {
//            log.info("所有任务都已完成");
//            return;
//        }
//        for (Task task : tasks) {
//            log.info("开始执行任务 task:{}", task);
//            downloaderMap.get(task.getSite()).execute(task);
//        }
//    }
//
//
//}
//
//
