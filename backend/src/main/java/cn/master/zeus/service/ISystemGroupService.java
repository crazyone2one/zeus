package cn.master.zeus.service;

import cn.master.zeus.dto.request.GroupRequest;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.entity.SystemGroup;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ISystemGroupService extends IService<SystemGroup> {

    List<SystemGroup> getGroupsByType(GroupRequest request);

    List<SystemGroup> getWorkspaceMemberGroups(String workspaceId, String userId);

    long checkSourceRole(String sourceId, String userId, String groupId);

    List<Map<String, Object>> getAllUserGroup(String userId);

    List<SystemGroup> getGroupByType(EditGroupRequest request);
}
