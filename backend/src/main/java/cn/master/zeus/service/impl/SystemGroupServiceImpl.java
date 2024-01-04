package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.dto.request.GroupRequest;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.mapper.SystemGroupMapper;
import cn.master.zeus.service.ISystemGroupService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;
import static cn.master.zeus.entity.table.WorkspaceTableDef.WORKSPACE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupMapper, SystemGroup> implements ISystemGroupService {
    private static final String GLOBAL = "global";

    @Override
    public List<SystemGroup> getGroupsByType(GroupRequest request) {
        String resourceId = request.getResourceId();
        String type = request.getType();
        List<String> scopeList = Arrays.asList(GLOBAL, resourceId);
        if (StringUtils.equals(type, UserGroupType.PROJECT) && StringUtils.isNotBlank(request.getProjectId())) {
            scopeList = Arrays.asList(GLOBAL, resourceId, request.getProjectId());
        }
        return queryChain().where(SYSTEM_GROUP.SCOPE_ID.in(scopeList).and(SYSTEM_GROUP.TYPE.eq(type))).list();
    }

    @Override
    public List<SystemGroup> getWorkspaceMemberGroups(String workspaceId, String userId) {
        QueryChain<SystemGroup> queryChain = queryChain().select(SYSTEM_GROUP.ALL_COLUMNS).from(WORKSPACE)
                .join(USER_GROUP).on(WORKSPACE.ID.eq(USER_GROUP.SOURCE_ID))
                .join(SYSTEM_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .where(WORKSPACE.ID.eq(workspaceId).and(USER_GROUP.USER_ID.eq(userId)));
        return mapper.selectListByQueryAs(queryChain, SystemGroup.class);
    }
}
