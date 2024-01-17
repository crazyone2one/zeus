package cn.master.zeus.service.impl;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.CustomFieldResourceDTO;
import cn.master.zeus.entity.CustomFieldTestCase;
import cn.master.zeus.mapper.CustomFieldTestCaseMapper;
import cn.master.zeus.service.ICustomFieldTestCaseService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.master.zeus.entity.table.CustomFieldTestCaseTableDef.CUSTOM_FIELD_TEST_CASE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class CustomFieldTestCaseServiceImpl extends ServiceImpl<CustomFieldTestCaseMapper, CustomFieldTestCase> implements ICustomFieldTestCaseService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFields(String resourceId, List<CustomFieldResourceDTO> addFields) {
        if (CollectionUtils.isNotEmpty(addFields)) {
            checkInit();
            addFields.forEach(field -> {
                createOrUpdateFields(resourceId, field);
            });
        }
    }

    @Override
    public Map<String, List<CustomFieldDao>> getMapByResourceIdsForList(List<String> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new HashMap<>();
        }
        List<CustomFieldResourceDTO> customFieldResources = getByResourceIdsForList(resourceIds);
        return getFieldMap(customFieldResources);
    }

    private List<CustomFieldResourceDTO> getByResourceIdsForList(List<String> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return new ArrayList<>();
        }
        QueryChain<CustomFieldTestCase> queryChain = queryChain().select(CUSTOM_FIELD_TEST_CASE.RESOURCE_ID, CUSTOM_FIELD_TEST_CASE.FIELD_ID, CUSTOM_FIELD_TEST_CASE.VALUE)
                .where(CUSTOM_FIELD_TEST_CASE.RESOURCE_ID.in(resourceIds));
        return mapper.selectListByQueryAs(queryChain, CustomFieldResourceDTO.class);
    }

    private Map<String, List<CustomFieldDao>> getFieldMap(List<CustomFieldResourceDTO> customFieldResources) {
        Map<String, List<CustomFieldDao>> fieldMap = new HashMap<>();
        customFieldResources.forEach(i -> {
            List<CustomFieldDao> fields = fieldMap.get(i.getResourceId());
            if (fields == null) {
                fields = new ArrayList<>();
            }
            CustomFieldDao customFieldDao = new CustomFieldDao();
            customFieldDao.setId(i.getFieldId());
            customFieldDao.setValue(i.getValue());
            customFieldDao.setTextValue(i.getTextValue());
            fields.add(customFieldDao);
            fieldMap.put(i.getResourceId(), fields);
        });
        return fieldMap;
    }

    private void createOrUpdateFields(String resourceId, CustomFieldResourceDTO field) {
        long count = countFieldResource(resourceId, field.getFieldId());
        field.setResourceId(resourceId);
        if (count > 0) {
            UpdateChain.of(CustomFieldTestCase.class)
                    .set(CUSTOM_FIELD_TEST_CASE.VALUE, field.getValue())
                    .set(CUSTOM_FIELD_TEST_CASE.TEXT_VALUE, field.getTextValue())
                    .where(CUSTOM_FIELD_TEST_CASE.RESOURCE_ID.eq(field.getResourceId()).and(CUSTOM_FIELD_TEST_CASE.FIELD_ID.eq(field.getFieldId())))
                    .update();
        } else {
            mapper.insert(CustomFieldTestCase.builder().resourceId(field.getResourceId()).fieldId(field.getFieldId()).value(field.getValue()).textValue(field.getTextValue()).build());
        }
    }

    private long countFieldResource(String resourceId, String fieldId) {
        QueryChain<CustomFieldTestCase> queryChain = queryChain().where(CUSTOM_FIELD_TEST_CASE.RESOURCE_ID.eq(resourceId).and(CUSTOM_FIELD_TEST_CASE.FIELD_ID.eq(fieldId)));
        return mapper.selectCountByQuery(queryChain);
    }

    private void checkInit() {

    }
}
