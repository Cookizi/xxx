package top.cookizi.xxx.service.downloader;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import top.cookizi.xxx.bean.DetailPageStatus;
import top.cookizi.xxx.bean.VideoPage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jsoup.Jsoup.connect;

@Component
public class PorntopicDownloader extends Downloader {

    @Override
    public VideoPage getPage(VideoPage page) throws IOException {
        Document document = connect(page.getPageUrl()).get();

        String href = document.select("div.info div.item").last().select("a").attr("href");
        String[] split = href.split("\\?");
        String videoSrc = split[1];
        Map<String, String> params = Arrays.stream(split[2].split("&")).map(x -> x.split("=")).collect(Collectors.toMap(x -> x[0], x -> x[1]));
        String filename = params.get("download_filename");

        page.setVideoPath(videoSrc);
        page.setFilename(filename);
        page.setCreatedAt(new Date());
        page.setStatus(DetailPageStatus.SUCCESS.getCode());

        return page;
    }

    @Override
    protected List<String> getPageInfos(Document document) {
        return document.select("#list_videos_common_videos_list_items div.item a").stream()
                .map(a -> a.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected void setDownloader() {
        downloaderMap.put("porntopic.com", this);
    }
}


