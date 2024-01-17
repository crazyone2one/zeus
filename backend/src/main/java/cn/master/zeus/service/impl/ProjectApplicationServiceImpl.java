package cn.master.zeus.service.impl;

import cn.master.zeus.dto.ProjectConfig;
import cn.master.zeus.entity.ProjectApplication;
import cn.master.zeus.mapper.ProjectApplicationMapper;
import cn.master.zeus.service.IProjectApplicationService;
import com.google.common.base.CaseFormat;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.ProjectApplicationTableDef.PROJECT_APPLICATION;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Slf4j
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication> implements IProjectApplicationService {

    @Override
    public ProjectApplication getProjectApplication(String projectId, String type) {
        List<ProjectApplication> projectApplications = queryChain().where(PROJECT_APPLICATION.PROJECT_ID.eq(projectId).and(PROJECT_APPLICATION.TYPE.eq(type))).list();
        if (Objects.isNull(projectApplications) || projectApplications.isEmpty()) {
            return new ProjectApplication();
        }
        return projectApplications.get(0);
    }

    @Override
    public ProjectConfig getSpecificTypeValue(String projectId, String type) {
        ProjectApplication application = this.getProjectApplication(projectId, type);
        ProjectConfig config = new ProjectConfig();
        Class<? extends ProjectConfig> clazz = config.getClass();
        Field field;
        try {
            field = clazz.getDeclaredField(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, type));
            try {
                field.setAccessible(true);
                String name = field.getName();
                Method method = config.getClass().getMethod("set" + StringUtils.capitalize(name), field.getType());
                if (StringUtils.isNotBlank(application.getTypeValue())) {
                    method.invoke(config, valueOf(field.getType(), application.getTypeValue()));
                }
            } catch (Exception e) {
                log.error("get project config error.");
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return config;
        }
        return config;
    }

    private Object valueOf(Class<?> type, String value) {
        //todo 其他类型
        if (type == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == Integer.class) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return 0;
            }
        }
        return value;
    }
}
