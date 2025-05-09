package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import top.yihoxu.ypicturebackend.model.entity.SpaceUser;
import top.yihoxu.ypicturebackend.model.vo.SpaceUserVO;

import java.util.List;

/**
 * @author hushi
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-05-08 14:14:02
 */
public interface SpaceUserService extends IService<SpaceUser> {


    /**
     * 获取单个封装类
     *
     * @param spaceUser
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser);


    /**
     * 构造查询条件
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间成员封装类
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
