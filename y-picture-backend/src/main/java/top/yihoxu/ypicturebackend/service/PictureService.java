package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.common.DeleteRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.*;
import top.yihoxu.ypicturebackend.model.entity.Picture;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;
import top.yihoxu.ypicturebackend.model.vo.PictureGradVO;

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
     *
     * @param inputSource          输入源
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 构建查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 列表对象转为列表封装类
     *
     * @param pictureList
     * @return
     */
    List<PictureVO> listUsersVO(List<Picture> pictureList, HttpServletRequest request);


    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    Boolean doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);


    /**
     * 填充图片审核
     *
     * @param picture
     * @param loginUser
     */
    void fillPictureReviewStatus(Picture picture, User loginUser);

    /**
     * 删除图片
     *
     * @param deleteRequest
     * @param loginUser
     */
    boolean deletePicture(DeleteRequest deleteRequest, User loginUser);


    /**
     * 批量抓取图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    List<PictureGradVO> uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 编辑图片（用户）
     * @param pictureEditRequest
     * @param loginUser
     * @return
     */
    Boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser);
}
