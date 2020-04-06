package top.cookizi.xxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.cookizi.xxx.bean.Task;

import java.util.List;

@Repository
public interface TaskMapper extends BaseMapper<Task> {
    @Select("select distinct category from task where site='${site}' order by category")
    List<String> listCategoryBySite(@Param("site") String site);

    @Select(("select distinct site from task order by site"))
    List<String> listSite();

}
