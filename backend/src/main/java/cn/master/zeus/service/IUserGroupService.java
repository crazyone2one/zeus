package cn.master.zeus.service;

import cn.master.zeus.dto.UserGroupDTO;
import cn.master.zeus.entity.UserGroup;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IUserGroupService extends IService<UserGroup> {

    List<UserGroupDTO> getUserGroup(String userId, String projectId);
}
