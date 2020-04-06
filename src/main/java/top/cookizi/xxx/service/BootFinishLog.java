package top.cookizi.xxx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.VideoPageMapper;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class BootFinishLog implements ApplicationRunner {
    @Autowired
    private BlockingQueue<Runnable> queue;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private DetailPageDownloader detailPageDownloader;

    @Override
    public void run(ApplicationArguments args) {
        log.info("服务已启动, url=http://localhost:8080");
        AtomicBoolean hasException = new AtomicBoolean(false);
        new Thread(() -> {
            Runnable take = null;
            List<VideoPage> videoPageList = videoPageMapper.selectList(new QueryWrapper<VideoPage>().eq("status", 0));
            videoPageList.forEach(x -> detailPageDownloader.download(x, -1, -1));
            while (true) {
                try {
                    if (hasException.get()) {
                        assert take != null;
                        queue.put(take);
                        hasException.set(false);
                        continue;
                    }
                    take = queue.take();
                    log.info("队列中剩余数量:{}",queue.size());
                    take.run();
                    hasException.set(false);
                } catch (Exception e) {
                    log.error("保存或者更新内容失败,失败信息={}", e.getMessage());
                    hasException.set(true);
                }
            }
        }, "mapperQueue").start();
    }
}
