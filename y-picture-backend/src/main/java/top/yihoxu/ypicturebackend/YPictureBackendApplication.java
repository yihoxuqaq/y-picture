package top.yihoxu.ypicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("top.yihoxu.ypicturebackend.mapper")
public class YPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YPictureBackendApplication.class, args);
    }

}
