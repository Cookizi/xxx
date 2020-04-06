package top.cookizi.xxx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
@MapperScan("top.cookizi.xxx.mapper")
public class XxxApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxxApplication.class, args);
    }

}
