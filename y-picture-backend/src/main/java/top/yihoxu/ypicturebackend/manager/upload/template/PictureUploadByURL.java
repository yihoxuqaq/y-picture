package top.yihoxu.ypicturebackend.manager.upload.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.springframework.stereotype.Service;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/5  15:56
 * @description 图片上传（URL）
 */
@Service
public class PictureUploadByURL extends PictureUploadTemplate {
    /**
     * 校验URL参数
     *
     * @param inputSource
     */
    @Override
    public void validPicture(Object inputSource) {
        String fileUrl = ((String) inputSource);
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        //1、验证URL
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2、验证URL协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR);
        HttpResponse response = null;
        try {
            //3.发送head请求验证文件是否存在
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            //如果没有正常返回则，无需其他判断
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                return;
            }
            //4、校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                //允许图片类型
                final List<String> ALLOW_TYPE = Arrays.asList("image/jpeg", "image/png", "image/webp","application/xml");
                ThrowUtils.throwIf(!ALLOW_TYPE.contains(contentType.toLowerCase()), ErrorCode.PARAMS_ERROR, "图片类型不符合");

            }
            //5、验证文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long ALLOW_SIZE = 3 * 1024 * 1024;
                    ThrowUtils.throwIf(contentLength > ALLOW_SIZE, ErrorCode.PARAMS_ERROR);
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }

            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public String getOriginalFilename(Object inputSource) {
        String filePath = ((String) inputSource);
        return FileUtil.mainName(filePath);
    }

    @Override
    public void processFile(Object inputSource, File file) throws IOException {
        String filePath = ((String) inputSource);
        HttpUtil.downloadFile(filePath, file);
    }

}
