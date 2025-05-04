package top.yihoxu.ypicturebackend.service;

import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.entity.User;

/**
* @author hushi
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-05-04 13:37:54
*/
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long spaceAdd(SpaceAddRequest spaceAddRequest, User loginUser);


    /**
     * 填充空间
     * @param space
     */
    void fillSpace(Space space);
}
