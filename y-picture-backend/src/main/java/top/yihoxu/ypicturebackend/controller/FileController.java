package top.yihoxu.ypicturebackend.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.annotation.AuthCheck;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.ResultUtils;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.manager.upload.CosManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author yihoxu
 * @date 2025/5/1  17:07
 * @description 通用文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private CosManager cosManager;

    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        //文件目录
        String originalFilename = multipartFile.getOriginalFilename();
        String filePath = String.format("/test/%s", originalFilename);
        File file = null;
        try {
            //创建一个临时目录或文件
            file = File.createTempFile(filePath, null);
            //将mvc中上传的文件输出到指定目录中
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            //返回可访问地址
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload error,filepath=" + filePath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传文件失败");
        } finally {
            if (file != null) {
                //删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error,filepath={}", filePath);
                }
            }
        }

    }
    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}
