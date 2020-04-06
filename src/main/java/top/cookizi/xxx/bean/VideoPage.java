package top.cookizi.xxx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("video_page")
public class VideoPage {
    @TableId(value = "id",type = IdType.AUTO)
    Integer id;
    String pageUrl;
    String videoPath;
    String filename;
    Integer taskId;
    Date createdAt;
    Integer status = 0;
}
