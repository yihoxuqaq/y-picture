package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureReviewRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureUploadRequest;
import top.yihoxu.ypicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hushi
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-05-02 13:25:21
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 构建查询条件
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 列表对象转为列表封装类
     * @param pictureList
     * @return
     */
    List<PictureVO> listUsersVO(List<Picture> pictureList, HttpServletRequest request);


    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    Boolean doPictureReview(PictureReviewRequest pictureReviewRequest,User loginUser);


    /**
     * 填充图片审核
     * @param picture
     * @param loginUser
     */
    void fillPictureReviewStatus(Picture picture,User loginUser);
}
