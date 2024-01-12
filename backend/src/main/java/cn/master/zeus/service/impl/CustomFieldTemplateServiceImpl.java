package cn.master.zeus.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.mapper.CustomFieldTemplateMapper;
import cn.master.zeus.service.ICustomFieldTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.master.zeus.entity.table.CustomFieldTemplateTableDef.CUSTOM_FIELD_TEMPLATE;

/**
 *  服务层实现。
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
}
