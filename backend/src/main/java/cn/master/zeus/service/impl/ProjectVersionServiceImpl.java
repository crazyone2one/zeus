package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.ProjectVersion;
import cn.master.zeus.mapper.ProjectVersionMapper;
import cn.master.zeus.service.IProjectVersionService;
import org.springframework.stereotype.Service;

import static cn.master.zeus.entity.table.ProjectVersionTableDef.PROJECT_VERSION;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ProjectVersionServiceImpl extends ServiceImpl<ProjectVersionMapper, ProjectVersion> implements IProjectVersionService {

    @Override
    public String getDefaultVersion(String projectId) {
        ProjectVersion projectVersion = queryChain().where(PROJECT_VERSION.PROJECT_ID.eq(projectId)
                .and(PROJECT_VERSION.STATUS.eq("open"))
                .and(PROJECT_VERSION.LATEST.eq(true))).limit(1).one();
        return projectVersion.getId();
    }
}
