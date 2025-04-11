package top.yihoxu.ypicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
public class PictureUpload extends PictureUploadTemplate {
    @Override
    protected void processFile(Object fileSource, File file) throws IOException {
        MultipartFile multipartFile = (MultipartFile) fileSource;
        multipartFile.transferTo(file);
    }

    @Override
    public String getOriginalFilename(Object fileSource) {
        MultipartFile multipartFile = (MultipartFile) fileSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    public void validPicture(Object fileSource) {
        MultipartFile multipartFile = (MultipartFile) fileSource;
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        //1、校验文件大小
        long fileSize = multipartFile.getSize();
        final long FILE_MIN = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 2 * FILE_MIN, ErrorCode.PARAMS_ERROR, "文件大小不能超过2M");

        //2.检验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FILE_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FILE_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }
}
