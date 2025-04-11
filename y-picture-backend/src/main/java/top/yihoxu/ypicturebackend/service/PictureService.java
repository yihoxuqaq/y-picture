package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureReviewRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureUploadByBatchRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureUploadRequest;
import top.yihoxu.ypicturebackend.model.dto.user.UserQueryRequest;
import top.yihoxu.ypicturebackend.model.pojo.Picture;
import top.yihoxu.ypicturebackend.model.pojo.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;


/**
 * @author hushi
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-04-09 20:26:24
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param fileSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object fileSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);


    /**
     * 获取图片拖敏信息并关联用户信息
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片脱敏信息
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 校验图片
     *
     * @param picture
     */
    void validPicture(Picture picture);


    /**
     *
     * @param pictureReviewRequest 审核参数
     * @param loginUser 登录用户
     */
    void doPictureReview(PictureReviewRequest  pictureReviewRequest, User loginUser);

    /**
     * 上传更新编辑图片是自动填充审核参数
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture,User loginUser);
}
