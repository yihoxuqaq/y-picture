package top.yihoxu.ypicturebackend.manager.upload.template;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/5  15:51
 * @description 上传图片
 */
@Service
public class PictureUpload extends PictureUploadTemplate {


    /**
     * 校验图片参数
     *
     * @param inputSource
     */
    @Override
    public void validPicture(Object inputSource) {
        MultipartFile multipartFile = ((MultipartFile) inputSource);
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        long fileSize = multipartFile.getSize();
        //校验文件大小
        final long MAX_UPLOAD_SIZE = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 3 * MAX_UPLOAD_SIZE, ErrorCode.PARAMS_ERROR, "文件最大不能超过 3M");
        //校验文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        final List<String> UPLOAD_TYPE = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!UPLOAD_TYPE.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }


    @Override
    public String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = ((MultipartFile) inputSource);
        return multipartFile.getOriginalFilename();
    }

    @Override
    public void processFile(Object inputSource, File file) throws IOException {
        MultipartFile multipartFile=((MultipartFile) inputSource);
        multipartFile.transferTo(file);

    }
}
