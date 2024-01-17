package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.UserGroupType;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.entity.*;
import cn.master.zeus.mapper.TestPlanMapper;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.BaseCheckPermissionService;
import cn.master.zeus.service.ISystemUserService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.TestPlanProjectTableDef.TEST_PLAN_PROJECT;
import static cn.master.zeus.entity.table.TestPlanTableDef.TEST_PLAN;
import static com.mybatisflex.core.query.QueryMethods.count;
import static com.mybatisflex.core.query.QueryMethods.sum;

/**
 * @author Created by 11's papa on 01/10/2024
 **/
@Service
@RequiredArgsConstructor
public class BaseCheckPermissionServiceImpl implements BaseCheckPermissionService {

    private final UserGroupMapper userGroupMapper;
    private final ISystemUserService systemUserService;
    private final TestPlanMapper testPlanMapper;

    @Override
    public void checkProjectBelongToWorkspace(String projectId, String workspaceId) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(projectId)).one();
        if (Objects.isNull(project) || !StringUtils.equals(project.getWorkspaceId(), workspaceId)) {
            BusinessException.throwException("当前用户没有操作此项目的权限");
        }
    }

    @Override
    public void checkTestPlanOwner(String planId) {
        Set<String> projectIds = getUserRelatedProjectIds();
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        long result = checkPlanIsHave(planId, projectIds);
        if (result == 0) {
            BusinessException.throwException("当前用户没有操作此计划的权限");
        }
    }

    private long checkPlanIsHave(String planId, Set<String> projectIds) {
        QueryChain<TestPlanProject> queryChain = QueryChain.of(TestPlanProject.class).select(sum("c")).from(
                QueryChain.of(TestPlanProject.class).select(count(String.valueOf(1)).as("c"))
                        .from(TEST_PLAN_PROJECT, PROJECT)
                        .where(PROJECT.ID.eq(TEST_PLAN_PROJECT.PROJECT_ID).and(TEST_PLAN_PROJECT.TEST_PLAN_ID.eq(planId)))
                        .and(PROJECT.ID.in(projectIds))
                        .union(
                                QueryChain.of(TestPlan.class).select(count(String.valueOf(1)).as("c"))
                                        .from(TEST_PLAN, PROJECT)
                                        .where(TEST_PLAN.PROJECT_ID.eq(PROJECT.ID).and(TEST_PLAN.ID.eq(planId)))
                                        .and(PROJECT.ID.in(projectIds))
                        )
        ).as("tmp");

        return testPlanMapper.selectCountByQuery(queryChain);
    }

    @Override
    public Set<String> getUserRelatedProjectIds() {
        if (SessionUtils.getUserId() != null && userGroupMapper.isSuperUser(SessionUtils.getUserId())) {
            List<Project> projects = QueryChain.of(Project.class).list();
            if (CollectionUtils.isNotEmpty(projects)) {
                return projects.stream().map(Project::getId).collect(Collectors.toSet());
            }
        }
        UserDTO userDTO = systemUserService.getUserDTO(SessionUtils.getUserId());
        List<String> groupIds = userDTO.getGroups().stream().filter(g -> StringUtils.equals(g.getType(), UserGroupType.PROJECT)).map(SystemGroup::getId).toList();
        return userDTO.getUserGroups().stream().filter(userGroup -> groupIds.contains(userGroup.getGroupId())).map(UserGroup::getSourceId).collect(Collectors.toSet());
    }

    @Override
    public void checkProjectOwner(String projectId) {
        if (SessionUtils.getUserId() != null && userGroupMapper.isSuperUser(SessionUtils.getUserId())) {
            return;
        }
        Set<String> projectIds = getUserRelatedProjectIds();
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        if (!projectIds.contains(projectId)) {
            BusinessException.throwException("当前用户没有操作此项目的权限");
        }
    }
}
