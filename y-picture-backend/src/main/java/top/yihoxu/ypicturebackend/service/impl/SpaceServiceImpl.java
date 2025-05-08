package top.yihoxu.ypicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.support.TransactionTemplate;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.enums.SpaceLevelEnum;
import top.yihoxu.ypicturebackend.enums.SpaceTypeEnum;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.entity.Space;
import top.yihoxu.ypicturebackend.model.entity.SpaceUser;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.mapper.SpaceMapper;
import org.springframework.stereotype.Service;
import top.yihoxu.ypicturebackend.service.SpaceUserService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;

/**
 * @author hushi
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-05-04 13:37:54
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public long spaceAdd(SpaceAddRequest spaceAddRequest, User loginUser) {
        String spaceName = spaceAddRequest.getSpaceName();
        Integer spaceLevel = spaceAddRequest.getSpaceLevel();
        Integer spaceType = spaceAddRequest.getSpaceType();
        if (spaceName.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if (spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if (!SpaceLevelEnum.COMMON.equals(spaceLevelEnum) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "非管理员只能创建普通级别");
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getSpaceTypeEnumByValue(spaceType);
        if (spaceTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不存在");
        }
        boolean exists = this.lambdaQuery()
                .eq(Space::getUserId, loginUser.getId())
                .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                .exists();
        if (exists) {
            String format = String.format("该账户下已存在一个%S", spaceTypeEnum.getText());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, format);
        }
        Space space = new Space();
        transactionTemplate.execute(status -> {
            BeanUtil.copyProperties(spaceAddRequest, space);
            fillSpace(space);
            space.setUserId(loginUser.getId());
            boolean save = this.save(space);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "创建空间失败");
            if (SpaceTypeEnum.TEAM.equals(spaceTypeEnum)) {
                SpaceUser spaceUser = new SpaceUser();
                spaceUser.setSpaceId(space.getId());
                spaceUser.setUserId(loginUser.getId());
                spaceUser.setSpaceRole(UserConstant.ADMIN_ROLE);
                boolean saveSpaceUser = spaceUserService.save(spaceUser);
                ThrowUtils.throwIf(!saveSpaceUser, ErrorCode.OPERATION_ERROR);
            }
            return save;
        });
        return space.getId();
    }

    @Override
    public void fillSpace(Space space) {
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        space.setMaxCount(spaceLevelEnum.getMaxCount());
        space.setMaxSize(spaceLevelEnum.getMaxSize());
    }
}




