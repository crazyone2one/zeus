package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.AddMemberRequest;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.SystemUserMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.ISystemGroupService;
import cn.master.zeus.service.ISystemUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 * 用户信息表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements ISystemUserService {
    private final UserGroupMapper userGroupMapper;
    private final ISystemGroupService systemGroupService;

    @Override
    public List<SystemUser> getMemberList(QueryMemberRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        return mapper.selectListByQuery(query);
    }

    @Override
    public Page<SystemUser> getMemberPage(BaseRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper query = new QueryWrapper()
                .select("distinct *").from(
                        wrapper.select(SYSTEM_USER.ALL_COLUMNS)
                                .from(USER_GROUP).join(SYSTEM_USER).on(USER_GROUP.USER_ID.eq(SYSTEM_USER.ID))
                                .where(USER_GROUP.SOURCE_ID.eq(request.getWorkspaceId()))
                                .and(SYSTEM_USER.NAME.like(request.getName(), StringUtils.isNoneBlank(request.getName())))
                                .orderBy(USER_GROUP.UPDATE_TIME.desc())
                ).as("temp");
        Page<SystemUser> paginate = mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), query);
        List<SystemUser> records = paginate.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            records.forEach(u -> {
                List<SystemGroup> groups = systemGroupService.getWorkspaceMemberGroups(request.getWorkspaceId(), u.getId());
                groups.removeAll(Collections.singleton(null));
                u.setGroups(groups);
            });
        }
        return paginate;
    }

    @Override
    public List<SystemUser> getUserList() {
        return queryChain().orderBy(SYSTEM_USER.UPDATE_TIME.desc()).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(AddMemberRequest request) {
        if (CollectionUtils.isEmpty(request.getUserIds()) || CollectionUtils.isEmpty(request.getGroupIds())) {
            log.warn("user ids or group ids is empty.");
            return;
        }
        List<String> allUserIds = queryChain().list().stream().map(SystemUser::getId).toList();
        List<SystemGroup> wsGroups = QueryChain.of(SystemGroup.class).where(SYSTEM_GROUP.TYPE.eq(UserGroupType.WORKSPACE)).list();
        List<String> wsGroupIds = wsGroups.stream().map(SystemGroup::getId).toList();
        for (String userId : request.getUserIds()) {
            if (!allUserIds.contains(userId)) {
                log.warn("user id {} is not exist!", userId);
                continue;
            }
            List<UserGroup> userGroups = QueryChain.of(UserGroup.class).where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.in(request.getWorkspaceId()))).list();
            if (!userGroups.isEmpty()) {
                BusinessException.throwException("该用户已存在于当前成员列表中");
            }
            for (String groupId : request.getGroupIds()) {
                if (!wsGroupIds.contains(groupId)) {
                    log.warn("group id {} is not exist or not belong to workspace level.", groupId);
                    continue;
                }
                UserGroup userGroup = UserGroup.builder().groupId(groupId).sourceId(request.getWorkspaceId()).userId(userId).build();
                userGroupMapper.insert(userGroup);
            }
        }
    }
}
