package cn.master.zeus.service;

import cn.master.zeus.dto.ProjectConfig;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.ProjectApplication;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IProjectApplicationService extends IService<ProjectApplication> {
    ProjectApplication getProjectApplication(String projectId, String type);

    ProjectConfig getSpecificTypeValue(String projectId, String type);
}
