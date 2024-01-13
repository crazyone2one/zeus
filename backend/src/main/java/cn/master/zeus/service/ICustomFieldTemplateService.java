package cn.master.zeus.service;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.CustomFieldTemplateDao;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.CustomFieldTemplate;

import java.util.List;

/**
 * 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldTemplateService extends IService<CustomFieldTemplate> {
    void deleteByFieldId(String fieldId);

    void create(List<CustomFieldTemplate> customFields, String templateId, String scene);

    void deleteByTemplateId(String templateId);

    List<CustomFieldTemplate> getSystemFieldCreateTemplate(CustomFieldDao customField, String templateId);

    List<CustomFieldTemplateDao> getList(CustomFieldTemplate request);
}
