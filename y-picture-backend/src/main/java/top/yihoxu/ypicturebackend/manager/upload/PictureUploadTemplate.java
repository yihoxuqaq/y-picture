package top.yihoxu.ypicturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;
import top.yihoxu.ypicturebackend.config.CosClientConfig;
import top.yihoxu.ypicturebackend.manager.CosManager;
import top.yihoxu.ypicturebackend.model.dto.picture.UploadPictureResult;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;


    /**
     * 上传图片
     *
     * @param fileSource
     * @param uploadPathPrefix
     * @return
     */

    public final UploadPictureResult uploadPicture(Object fileSource, String uploadPathPrefix) {
        //校验图片
        validPicture(fileSource);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(fileSource);
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            //创建临时文件
            file = File.createTempFile(uploadPath, null);
            //处理文件来源
            processFile(fileSource, file);
            //上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                CIObject ciObject = objectList.get(0);
                CIObject thumbnail = ciObject;
                if (objectList.size() > 1) {
                    thumbnail = objectList.get(1);
                }
                //分装返回结果
                return getUploadPictureResult(originalFilename, ciObject, thumbnail, imageInfo);
            }
            //分装返回结果
            return getUploadPictureResult(imageInfo, originalFilename, file, uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.deleteTempFile(file);
        }
    }

    private UploadPictureResult getUploadPictureResult(String originalFilename, CIObject ciObject, CIObject thumbnail, ImageInfo imageInfo) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int width = ciObject.getWidth();
        int height = ciObject.getHeight();
        double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(ciObject.getSize().longValue());
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicScale(scale);
        uploadPictureResult.setPicColor(imageInfo.getAve());
        uploadPictureResult.setPicFormat(ciObject.getFormat());
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + ciObject.getKey());
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnail.getKey());
        return uploadPictureResult;
    }

    private UploadPictureResult getUploadPictureResult(ImageInfo imageInfo, String originalFilename, File file, String uploadPath) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicColor(imageInfo.getAve());
        uploadPictureResult.setPicScale(scale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

    /**
     * 处理文件来源
     *
     * @param fileSource
     * @param file
     */
    protected abstract void processFile(Object fileSource, File file) throws IOException;

    /**
     * 获取文件名称
     *
     * @param fileSource
     * @return
     */
    public abstract String getOriginalFilename(Object fileSource);

    /**
     * 校验图片
     *
     * @param fileSource 通过文件或者URL
     */
    public abstract void validPicture(Object fileSource);


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean delete = file.delete();
        if (!delete) {
            log.error("file delete error,filepath={}", file.getAbsolutePath());
        }
    }
}
