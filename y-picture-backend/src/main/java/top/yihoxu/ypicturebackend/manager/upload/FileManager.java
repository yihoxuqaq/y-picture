package top.yihoxu.ypicturebackend.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.config.CosClientConfig;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.manager.upload.dto.UploadPictureResult;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/1  17:02
 * @description 文件上传下载通用方法
 */
@Service
@Slf4j
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;


    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        //校验图片
        validPicture(multipartFile);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%S/%S", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            //创建临时文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //封装返回原图信息
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picHeight = imageInfo.getHeight();
            int pciWidth = imageInfo.getWidth();
            double picScale = NumberUtil.round(pciWidth * 1.0 / picHeight, 2).doubleValue();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(pciWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());

            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }


    }


    /**
     * 校验图片参数
     *
     * @param multipartFile
     */
    public void validPicture(MultipartFile multipartFile) {
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

    /**
     * 删除临时文件
     *
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        //删除临时文件
        boolean result = file.delete();
        if (!result) {
            log.error("delete file error,filepath={}", file.getAbsolutePath());
        }
    }

}
