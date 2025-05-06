package top.yihoxu.ypicturebackend.manager.upload.template;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.config.CosClientConfig;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.manager.upload.CosManager;
import top.yihoxu.ypicturebackend.manager.upload.dto.UploadPictureResult;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author yihoxu
 * @date 2025/5/1  17:02
 * @description 图片上传模版方法
 */

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;


    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        //校验图片
        validPicture(inputSource);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%S/%S", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            //创建临时文件
            file=File.createTempFile(uploadPath,null);
            processFile(inputSource,file);
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
     * @param inputSource
     */
    public abstract void validPicture(Object inputSource);


    /**
     * 获取路径
     * @param inputSource
     * @return
     */
    public abstract String getOriginalFilename(Object inputSource);

    public abstract void processFile(Object inputSource,File file) throws IOException;

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
