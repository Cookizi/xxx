package top.cookizi.xxx.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import top.cookizi.xxx.bean.DetailPageStatus;
import top.cookizi.xxx.bean.Task;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.TaskMapper;
import top.cookizi.xxx.mapper.VideoPageMapper;
import top.cookizi.xxx.service.downloader.Downloader;

import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class DetailPageDownloader {
    @Autowired
    @Qualifier("downloaderMapper")
    private Map<String, Downloader> downloaderMap;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    @Qualifier("detailPageDownloadExecutor")
    private Executor executor;

    public void download(VideoPage page, int currentPage, int detailPageNo) {
        executor.execute(() -> asyncDownload(page,currentPage,detailPageNo));
    }
    public VideoPage asyncDownload(VideoPage page, int currentPage, int detailPageNo){
        DetailPageStatus status = DetailPageStatus.SUCCESS;
        try {
            Task task = taskMapper.selectById(page.getTaskId());
            if (currentPage < 0) {
                log.info("重试页面url={}", page.getPageUrl());
            } else {
                log.info("正在处理第{}页,第{}条, 页面url={}", currentPage, detailPageNo, page.getPageUrl());
            }
            downloaderMap.get(task.getSite()).getPage(page);
        } catch (Exception e) {
            if (currentPage < 0) {

                log.error("重试url [失败] , 页面url={}失败信息={}", page.getPageUrl(), e.getMessage());
            }else {
                log.error("正在处理第{}页,第{}条, [失败], 页面url={}失败信息={}", currentPage, detailPageNo, page.getPageUrl(), e.getMessage());
            }
            status = DetailPageStatus.FAIL;
            page.setStatus(status.getCode());
        }

        videoPageMapper.updateById(page);
        return page;
    }
}

