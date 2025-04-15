package top.yihoxu.ypicturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceQueryRequest;
import top.yihoxu.ypicturebackend.model.pojo.Space;
import top.yihoxu.ypicturebackend.model.pojo.User;
import top.yihoxu.ypicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hushi
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-04-14 15:12:28
 */
public interface SpaceService extends IService<Space> {

    /**
     * 校验空间参数
     *
     * @param space
     */
    void validSpace(Space space, Boolean add);


    /**
     * 创建空间
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);


    /**
     * 构建查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间拖敏信息并关联用户信息
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);


    /**
     * 根据空间级别，自动填充限额
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 分页获取空间脱敏信息
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);
}
