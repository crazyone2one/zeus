package cn.master.zeus.service.impl;

import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.entity.Project;
import cn.master.zeus.service.BaseCheckPermissionService;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;

/**
 * @author Created by 11's papa on 01/10/2024
 **/
@Service
@RequiredArgsConstructor
public class BaseCheckPermissionServiceImpl implements BaseCheckPermissionService {

    @Override
    public void checkProjectBelongToWorkspace(String projectId, String workspaceId) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(projectId)).one();
        if (Objects.isNull(project) || !StringUtils.equals(project.getWorkspaceId(), workspaceId)) {
            BusinessException.throwException("当前用户没有操作此项目的权限");
        }
    }
}
