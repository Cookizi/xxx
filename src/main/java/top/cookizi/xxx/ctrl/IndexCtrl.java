package top.cookizi.xxx.ctrl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.cookizi.xxx.bean.Task;
import top.cookizi.xxx.bean.VideoPage;
import top.cookizi.xxx.mapper.TaskMapper;
import top.cookizi.xxx.mapper.VideoPageMapper;
import top.cookizi.xxx.service.DownloadService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class IndexCtrl {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private VideoPageMapper videoPageMapper;
    @Autowired
    private DownloadService downloadService;

    @GetMapping("get-site")
    @ResponseBody
    public List<String> getSite() {
        return taskMapper.listSite();
    }

    @GetMapping("/get-category")
    @ResponseBody
    public List<String> getCategory(@RequestParam("site") String site) {
        return taskMapper.listCategoryBySite(site);
    }

    @GetMapping("/get-task")
    @ResponseBody
    public Map<String, Object> getTasks(@RequestParam(value = "site", required = false) String site,
                                        @RequestParam(value = "category", required = false) String category,
                                        @RequestParam(value = "page", defaultValue = "1") Long page,
                                        @RequestParam(value = "rows", defaultValue = "10") Long rows) {

        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(site), "site", site)
                .eq(StringUtils.isNotBlank(category), "category", category);
        Page<Task> taskList = taskMapper.selectPage(new Page<>(page, rows), wrapper);
        return new HashMap<String, Object>() {{
            put("total", taskList.getTotal());
            put("rows", taskList.getRecords());
        }};
    }

    @GetMapping("/get-video-data")
    @ResponseBody
    public Map<String, Object> getVideoData(@RequestParam(value = "taskId") String taskId,
                                            @RequestParam(value = "page", defaultValue = "1") Long page,
                                            @RequestParam(value = "rows", defaultValue = "10") Long rows) {

        QueryWrapper<VideoPage> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId);
        Page<VideoPage> videoPages = videoPageMapper.selectPage(new Page<>(page, rows), wrapper);
        return new HashMap<String, Object>() {{
            put("total", videoPages.getTotal());
            put("rows", videoPages.getRecords());
        }};
    }

    @PostMapping("save-task")
    @ResponseBody
    @Transactional
    public Map<String, Object> saveTask(@RequestParam(value = "taskList") List<String> taskList) {
        QueryWrapper<Task> wrapper = new QueryWrapper<Task>().in("task_url", taskList);
        List<Task> existTaskList = taskMapper.selectList(wrapper);
        List<String> existTaskUrlList = existTaskList.stream().map(Task::getTaskUrl).collect(Collectors.toList());
        taskList.removeAll(existTaskUrlList);
        Pattern compile = Pattern.compile("http://(.*)/categories/(.*)/");
        taskList.forEach(x -> {
            Matcher matcher = compile.matcher(x);
            if (matcher.find()) {
                String site = matcher.group(1);
                String category = matcher.group(2);
                Task task = new Task();
                task.setTaskUrl(x);
                task.setCategory(category);
                task.setSite(site);
                taskMapper.insert(task);
            }
        });

        return new HashMap<>();
    }

    @PostMapping("start-task")
    @ResponseBody
    public Map<String, Object> startTask(@RequestParam("startPage") Integer startPage,
                                         @RequestParam(value = "endPage",required = false,defaultValue = ""+Integer.MAX_VALUE) Integer endPage,
                                         @RequestParam("taskId") Integer taskId) {
        downloadService.startTask(taskId, startPage, endPage);
        return new HashMap<String, Object>() {{
            put("code", "0");
        }};
    }

    @PostMapping("renew")
    @ResponseBody
    public Map<String,Object> renewVideoPath(@RequestParam("videoId")String videoId){
        return downloadService.renewVideoPath(videoId);
    }


}
