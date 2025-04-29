package top.yihoxu.ypicturebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YPictureBackendApplication {

    public static void main(String[] args) {
        System.out.println("http://localhost:8123/api/doc.html#/home");
        SpringApplication.run(YPictureBackendApplication.class, args);
    }

}
