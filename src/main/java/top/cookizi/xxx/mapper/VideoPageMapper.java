package top.cookizi.xxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.cookizi.xxx.bean.VideoPage;

@Repository
public interface VideoPageMapper extends BaseMapper<VideoPage> {

    @Select("select * from video_page where filename=#{filename} or page_url=#{pageUrl}")
    VideoPage hasFile(@Param("filename") String filename,@Param("pageUrl") String pageUrl);
}
