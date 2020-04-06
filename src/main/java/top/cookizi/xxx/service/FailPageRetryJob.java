package top.cookizi.xxx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.cookizi.xxx.bean.Task;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.TaskMapper;
import top.cookizi.xxx.mapper.VideoPageMapper;
import top.cookizi.xxx.service.downloader.Downloader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

@Slf4j
@Component
@EnableScheduling   // 1.开启定时任务
@Async
public class FailPageRetryJob {
    @Autowired
    @Qualifier("downloaderMapper")
    private Map<String, Downloader> downloaderMap;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private BlockingQueue<Runnable> queue;
    @Autowired
    @Qualifier("detailPageDownloadExecutor")
    private Executor executor;

    @Scheduled(fixedDelay = 60000)
    public void handle() {
        log.info("开始处理获取失败的连接");
        List<VideoPage> failedUrlList = videoPageMapper.selectList(new QueryWrapper<VideoPage>().in("status", -1));
        if (failedUrlList.isEmpty()) {
            log.info("没有获取失败的连接");
            return;
        }
        for (VideoPage page : failedUrlList) {
            executor.execute(() -> {
                try {
                    Task task = taskMapper.selectOne(new QueryWrapper<Task>().eq("id", page.getTaskId()));
                    log.info("开始处理失败连接:url={}", page.getPageUrl());
                    downloaderMap.get(task.getSite()).getPage(page);
                } catch (Exception e) {
                    log.error("[再次]获取连接信息失败, url={},失败信息={}", page.getPageUrl(), e.getMessage());
                }
                videoPageMapper.updateById(page);
            });
        }
        log.info("失败连接添加重试完成");
    }
}
