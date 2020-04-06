package top.cookizi.xxx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("task")
public class Task {
    @TableId(value = "id",type = IdType.AUTO)
    Integer id;
    String taskUrl;
    String site;
    String category;
    Integer currentPage = 1;
    Integer status = 0;//0:executing,1:finish

    @TableField(exist = false)
    Integer retry = 0;
}
