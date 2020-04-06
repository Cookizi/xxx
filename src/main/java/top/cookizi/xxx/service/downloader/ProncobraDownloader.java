package top.cookizi.xxx.service.downloader;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import top.cookizi.xxx.bean.DetailPageStatus;
import top.cookizi.xxx.bean.VideoPage;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.jsoup.Jsoup.connect;

@Component
public class ProncobraDownloader extends Downloader {
    @Override
    protected void setDownloader() {
        downloaderMap.put("porncobra.com", this);
    }

    @Override
    protected List<String> getPageInfos(Document document) {
        return document.select("#list_videos_common_videos_list_items div.item a").stream()
                .map(a -> a.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    public VideoPage getPage( VideoPage page) throws IOException {
        Document document = connect(page.getPageUrl()).get();

        Element element = document.select("div.item a[target='_blank']").get(0);
        String videoUrl = element.attr("href");
        String[] split = videoUrl.split("\\?");
        videoUrl = split[1];
        String filename = null;
        for (String kv : split[2].split("&")) {
            String[] kvs = kv.split("=");
            if ("download_filename".equals(kvs[0])) {
                filename = kvs[1].replaceAll("-", " ");
            }
        }
        if (StringUtils.isNotBlank(filename) && StringUtils.isNotBlank(videoUrl)) {
            page.setCreatedAt(new Date());
            page.setFilename(filename);
            page.setVideoPath(videoUrl);
            page.setStatus(DetailPageStatus.SUCCESS.getCode());
        }

        return page;
    }
}
