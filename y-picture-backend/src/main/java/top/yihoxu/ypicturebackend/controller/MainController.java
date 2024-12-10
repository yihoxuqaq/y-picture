package top.yihoxu.ypicturebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.ResultUtils;

/**
 * @Author dth
 * @Date 2024/12/9 14:34
 * @PackageName:top.yihoxu.ypicturebackend.controller
 * @ClassName: MainController
 * @Description:健康检查接口
 * @Version 1.0
 */
@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
