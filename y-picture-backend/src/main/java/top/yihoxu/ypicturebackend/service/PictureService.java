package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureQueryRequest;
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
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);


    /**
     * 获取图片拖敏信息并关联用户信息
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片脱敏信息
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO>getPictureVOPage(Page<Picture> picturePage,HttpServletRequest request);

    /**
     * 校验图片
     * @param picture
     */
   void validPicture(Picture picture);
}
