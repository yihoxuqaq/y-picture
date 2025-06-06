package top.yihoxu.ypicturebackend.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
     * 上传用户头像
     *
     * @param multipartFile
     * @return
     */
    public String uploadUserAvatar(MultipartFile multipartFile) {
        validPicture(multipartFile);
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String avatarPath = String.format("/userAvatar/%s.%s", uuid, FileUtil.getSuffix(originalFilename));
        File file = null;
        try {
            file = File.createTempFile(avatarPath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(avatarPath, file);
            return cosClientConfig.getHost() + avatarPath;
        } catch (IOException e) {
            log.error("用户头像上传失败", e);
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
     * 校验URL参数
     *
     * @param fileUrl
     */
    public void validPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        //1、验证URL
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2、验证URL协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") || !fileUrl.startsWith("https://"),
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
                final List<String> ALLOW_TYPE = Arrays.asList("image/jpeg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_TYPE.contains(contentType.toLowerCase()), ErrorCode.PARAMS_ERROR);

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
