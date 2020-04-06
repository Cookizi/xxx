package top.cookizi.xxx.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.cookizi.xxx.service.downloader.Downloader;

import java.util.Map;
import java.util.concurrent.*;

@Configuration
public class BeanConfig {

    @Bean
    public BlockingQueue<Runnable> getQueue() {
        return new PriorityBlockingQueue<>();
    }

    @Bean(name = "downloaderMapper")
    public Map<String, Downloader> getDownloader() {
        return new ConcurrentHashMap<>();
    }

    @Bean("listPageDownloadExecutor")
    public Executor listPageDownloadExecutor() {
        return new ThreadPoolExecutor(2, 10, 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
    @Bean("detailPageDownloadExecutor")
    public Executor detailPageDownloadExecutor() {
        return new ThreadPoolExecutor(20, 50, 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
