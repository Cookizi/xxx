package top.cookizi.xxx.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import top.cookizi.xxx.bean.DetailPageStatus;
import top.cookizi.xxx.bean.Task;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.TaskMapper;
import top.cookizi.xxx.mapper.VideoPageMapper;
import top.cookizi.xxx.service.downloader.Downloader;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DownloadService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    @Qualifier("downloaderMapper")
    private Map<String, Downloader> downloaderMap;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private DetailPageDownloader detailPageDownloader;

    public void startTask(Integer taskId, Integer startPage, Integer endPage) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            log.warn("no task for task id = {}", taskId);
            return;
        }
        new Thread(() -> downloaderMap.get(task.getSite()).execute(task, startPage, endPage)).start();

    }

    public Map<String, Object> renewVideoPath(String videoId) {
        VideoPage videoPage = videoPageMapper.selectById(videoId);

        videoPage = detailPageDownloader.asyncDownload(videoPage, -1, -1);

        HashMap<String, Object> resp = new HashMap<>();
        resp.put("code", videoPage.getStatus());
        resp.put("data", videoPage);
        return resp;
    }
}
