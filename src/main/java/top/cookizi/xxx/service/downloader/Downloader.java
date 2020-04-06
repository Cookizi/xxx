package top.cookizi.xxx.service.downloader;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import top.cookizi.xxx.bean.Task;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.TaskMapper;
import top.cookizi.xxx.mapper.VideoPageMapper;
import top.cookizi.xxx.service.DetailPageDownloader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class Downloader {
    @Autowired
    @Qualifier("downloaderMapper")
    protected Map<String, Downloader> downloaderMap;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private BlockingQueue<Runnable> queue;
    @Autowired
    @Qualifier("listPageDownloadExecutor")
    private Executor executor;

    LinkedTransferQueue<Task> retryQueue;

    private int execute(Task task, int currentPage) {
        int code = 200;

        String url = task.getTaskUrl()
                + "?mode=async&function=get_block&block_id=list_videos_common_videos_list&sort_by=post_date&from="
                + currentPage + "&_=" + System.currentTimeMillis();
        log.info("当前第{}页, url:{}", currentPage, url);

        try {
            Document document = Jsoup.connect(url).get();
            List<String> pages = getPageInfos(document);

            getPageAndSave(pages, task, currentPage);
            task.setCurrentPage(currentPage);
            taskMapper.updateById(task);

        } catch (HttpStatusException e) {
            code = e.getStatusCode();
        } catch (Exception e) {
            log.error("获取页面失败,重试次数={}, url={},原因:{}", task.getRetry(), url, e.getMessage());
            Integer retry = task.getRetry();
            task.setRetry(++retry);
            retryQueue.add(task);
        }

        return code;
    }

    public void getPageAndSave(List<String> pageList, Task task, int currentPage) {

        AtomicInteger i = new AtomicInteger(1);
        for (String url : pageList) {

            final VideoPage page = new VideoPage();
            page.setPageUrl(url);
            page.setTaskId(task.getId());
            try {
                if (videoPageMapper.hasFile(page.getFilename(), page.getPageUrl()) != null) {
                    log.info("文件已存在,url={}, filename={}", page.getPageUrl(), page.getFilename());
                    return;
                }
                videoPageMapper.insert(page);
            } catch (Exception e) {
                log.error("获取视频页面失败, url={},错误原因:{}", page.getPageUrl(), e.getMessage());
                queue.add(() -> videoPageMapper.insert(page));
            }
            detailPageDownloader.download(page, currentPage, i.getAndIncrement());

        }

    }

    @Autowired
    private DetailPageDownloader detailPageDownloader;


    @PostConstruct
    protected abstract void setDownloader();

    protected abstract List<String> getPageInfos(Document document);

    public abstract VideoPage getPage(VideoPage page) throws IOException;


    public void execute(Task task, Integer startPage, Integer endPage) {

        retryQueue = new LinkedTransferQueue<>();


        for (int currentPage = startPage; currentPage <= endPage; currentPage++) {

            int code = execute(task, currentPage);
            if (code == 404) {
                task.setStatus(1);
                taskMapper.updateById(task);
                log.info("执行任务结束, task={}", task);
                break;
            }
        }

        while (!retryQueue.isEmpty()) {
            Task failTask = retryQueue.poll();
            execute(failTask, task.getCurrentPage());
        }

    }


}
