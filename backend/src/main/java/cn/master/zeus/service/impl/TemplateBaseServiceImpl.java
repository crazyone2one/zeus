package cn.master.zeus.service.impl;

import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.entity.Project;
import cn.master.zeus.service.TemplateBaseService;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;

/**
 * @author Created by 11's papa on 01/12/2024
 **/
@Service
public class TemplateBaseServiceImpl implements TemplateBaseService {
    @Override
    public void checkTemplateUsed(String templateId) {
        List<Project> projects = QueryChain.of(Project.class).where(PROJECT.CASE_TEMPLATE_ID.eq(templateId)).list();
        if (CollectionUtils.isNotEmpty(projects)) {
            StringBuilder tip = new StringBuilder();
            projects.forEach(item -> {
                tip.append(item.getName()).append(',');
            });
            tip.deleteCharAt(tip.length() - 1);
            BusinessException.throwException("该模板已关联项目：" + tip);
        }
    }
}
