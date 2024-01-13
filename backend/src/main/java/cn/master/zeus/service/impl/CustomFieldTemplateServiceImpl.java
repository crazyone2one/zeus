package cn.master.zeus.service.impl;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.CustomFieldTemplateDao;
import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.mapper.CustomFieldTemplateMapper;
import cn.master.zeus.service.ICustomFieldTemplateService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.CustomFieldTableDef.CUSTOM_FIELD;
import static cn.master.zeus.entity.table.CustomFieldTemplateTableDef.CUSTOM_FIELD_TEMPLATE;
import static com.mybatisflex.core.query.QueryMethods.max;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CustomFieldTemplateServiceImpl extends ServiceImpl<CustomFieldTemplateMapper, CustomFieldTemplate> implements ICustomFieldTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldId) {
        QueryChain<CustomFieldTemplate> where = queryChain().where(CUSTOM_FIELD_TEMPLATE.FIELD_ID.eq(fieldId));
        mapper.deleteByQuery(where);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<CustomFieldTemplate> customFields, String templateId, String scene) {
        if (CollectionUtils.isNotEmpty(customFields)) {
            final Long[] nextOrder = {getNextOrder(templateId, null)};
            customFields.forEach(item -> {
                item.setTemplateId(templateId);
                item.setScene(scene);
                if (item.getRequired() == null) {
                    item.setRequired(false);
                }
                nextOrder[0] += 5000;
                item.setOrder((int) nextOrder[0].longValue());
                mapper.insert(item);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByTemplateId(String templateId) {
        QueryChain<CustomFieldTemplate> where = queryChain().where(CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.eq(templateId));
        mapper.deleteByQuery(where);
    }

    @Override
    public List<CustomFieldTemplate> getSystemFieldCreateTemplate(CustomFieldDao customField, String templateId) {
        List<CustomFieldTemplate> fieldTemplates = queryChain().where(CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.eq(templateId)).list();
        for (CustomFieldTemplate fieldTemplate : fieldTemplates) {
            // 将全局字段替换成项目下的字段
            if (StringUtils.equals(fieldTemplate.getFieldId(), customField.getOriginGlobalId())) {
                fieldTemplate.setFieldId(customField.getId());
                break;
            }
        }
        return fieldTemplates;
    }

    @Override
    public List<CustomFieldTemplateDao> getList(CustomFieldTemplate request) {
        QueryChain<CustomFieldTemplate> queryChain = queryChain().select(CUSTOM_FIELD_TEMPLATE.FIELD_ID, CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID, CUSTOM_FIELD_TEMPLATE.REQUIRED,
                        CUSTOM_FIELD_TEMPLATE.DEFAULT_VALUE, CUSTOM_FIELD_TEMPLATE.CUSTOM_DATA, CUSTOM_FIELD_TEMPLATE.ID.as("id"),
                        CUSTOM_FIELD.NAME.as("name"), CUSTOM_FIELD.TYPE.as("type"), CUSTOM_FIELD.REMARK.as("remark"),
                        CUSTOM_FIELD.SYSTEM.as("system"), CUSTOM_FIELD.OPTIONS.as("options"))
                .from(CUSTOM_FIELD_TEMPLATE).innerJoin(CUSTOM_FIELD).on(CUSTOM_FIELD_TEMPLATE.FIELD_ID.eq(CUSTOM_FIELD.ID))
                .where(CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.eq(request.getTemplateId()))
                .orderBy(CUSTOM_FIELD_TEMPLATE.ORDER.asc());
        return mapper.selectListByQueryAs(queryChain, CustomFieldTemplateDao.class);
    }

    private long getNextOrder(String templateId, Integer baseOrder) {
        CustomFieldTemplate one = queryChain().select(max(CUSTOM_FIELD_TEMPLATE.ORDER)).where(CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.eq(templateId))
                .and(CUSTOM_FIELD_TEMPLATE.ORDER.gt(baseOrder).when(Objects.nonNull(baseOrder))).one();
        return one.getOrder().longValue();
    }
}
