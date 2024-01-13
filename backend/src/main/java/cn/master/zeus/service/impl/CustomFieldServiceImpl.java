package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.TemplateConstants;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.request.QueryCustomFieldRequest;
import cn.master.zeus.entity.CustomField;
import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.mapper.CustomFieldMapper;
import cn.master.zeus.service.ICustomFieldService;
import cn.master.zeus.service.ICustomFieldTemplateService;
import cn.master.zeus.service.ITestCaseTemplateService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.CustomFieldTableDef.CUSTOM_FIELD;
import static cn.master.zeus.entity.table.CustomFieldTemplateTableDef.CUSTOM_FIELD_TEMPLATE;
import static com.mybatisflex.core.query.QueryMethods.notExists;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Lazy))
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField> implements ICustomFieldService {
    private final ICustomFieldTemplateService customFieldTemplateService;
    @Lazy
    private final ITestCaseTemplateService testCaseTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(CustomField customField) {
        checkExist(customField);
        customField.setGlobal(false);
        customField.setThirdPart(false);
        String userId = SessionUtils.getUserId();
        customField.setCreateUser(userId);
        customField.setUpdateUser(userId);
        mapper.insert(customField);
        return customField.getId();
    }

    @Override
    public Page<CustomField> getPage(QueryCustomFieldRequest page) {
        QueryChain<CustomField> queryChain = queryChain(page);
        System.out.println("[[[[[");
        System.out.println(queryChain.toSQL());
        System.out.println("[[[[[");
        return mapper.paginate(Page.of(page.getPageNumber(), page.getPageSize()), queryChain);
    }

    @Override
    public Page<CustomField> listRelate(QueryCustomFieldRequest page) {
        List<String> templateContainIds = page.getTemplateContainIds();
        if (CollectionUtils.isEmpty(templateContainIds)) {
            templateContainIds = new ArrayList<>();
        }
        page.setTemplateContainIds(templateContainIds);
        QueryChain<CustomField> queryChain = queryChain(page);
        return mapper.paginate(Page.of(page.getPageNumber(), page.getPageSize()), queryChain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        mapper.deleteById(id);
        customFieldTemplateService.deleteByFieldId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomField(CustomField customField) {
        if (customField.getGlobal() != null && customField.getGlobal()) {
            CustomFieldDao customFieldDao = new CustomFieldDao();
            BeanUtils.copyProperties(customField, customFieldDao);
            customFieldDao.setOriginGlobalId(customField.getId());
            add(customFieldDao);
            if (StringUtils.equals(customField.getScene(), TemplateConstants.FieldTemplateScene.TEST_CASE.name())) {
                testCaseTemplateService.handleSystemFieldCreate(customFieldDao);
            } else if (StringUtils.equals(customField.getScene(), TemplateConstants.FieldTemplateScene.API.name())) {
                //apiTemplateService.handleSystemFieldCreate(customFieldDao);
            } else {
                //issueTemplateService.handleSystemFieldCreate(customFieldDao);
            }
        } else {
            checkExist(customField);
            mapper.update(customField);
        }
    }

    @Override
    public List<CustomFieldDao> getCustomFieldByTemplateId(String id) {
        List<CustomFieldTemplate> customFields = QueryChain.of(CustomFieldTemplate.class)
                .select(CUSTOM_FIELD_TEMPLATE.ID, CUSTOM_FIELD_TEMPLATE.FIELD_ID.as("fieldId")
                        , CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.as("templateId"), CUSTOM_FIELD_TEMPLATE.SCENE, CUSTOM_FIELD_TEMPLATE.REQUIRED
                        , CUSTOM_FIELD_TEMPLATE.ORDER, CUSTOM_FIELD_TEMPLATE.DEFAULT_VALUE, CUSTOM_FIELD_TEMPLATE.CUSTOM_DATA, CUSTOM_FIELD_TEMPLATE.KEY)
                .where(CUSTOM_FIELD_TEMPLATE.TEMPLATE_ID.eq(id)).orderBy(CUSTOM_FIELD_TEMPLATE.ORDER.asc()).list();
        List<String> fieldIds = customFields.stream().map(CustomFieldTemplate::getFieldId).toList();
        List<CustomField> fields = getFieldByIds(fieldIds);
        Map<String, CustomField> fieldMap = fields.stream().collect(Collectors.toMap(CustomField::getId, item -> item));
        List<CustomFieldDao> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(customFields)) {
            customFields.forEach((item) -> {
                CustomFieldDao customFieldDao = new CustomFieldDao();
                CustomField customField = fieldMap.get(item.getFieldId());
                BeanUtils.copyProperties(customField, customFieldDao);
                BeanUtils.copyProperties(item, customFieldDao);
                customFieldDao.setId(item.getFieldId());
                result.add(customFieldDao);
            });
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<CustomField> getFieldByIds(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return queryChain().select(CUSTOM_FIELD.ALL_COLUMNS).from(CUSTOM_FIELD.as("cf")).where(CUSTOM_FIELD.ID.in(ids)).list();
        }
        return new ArrayList<>();
    }

    @Override
    public List<CustomField> getDefaultField(QueryCustomFieldRequest request) {
        List<CustomField> workspaceSystemFields = queryChain().where(CUSTOM_FIELD.SYSTEM.eq(true).and(CUSTOM_FIELD.SCENE.eq(request.getScene()))
                .and(CUSTOM_FIELD.PROJECT_ID.eq(request.getProjectId()))).list();
        Set<String> workspaceSystemFieldNames = workspaceSystemFields.stream().map(CustomField::getName).collect(Collectors.toSet());
        List<CustomField> globalFields = getGlobalField(request.getScene());
        // 工作空间的系统字段加上全局的字段
        globalFields.forEach(item -> {
            if (!workspaceSystemFieldNames.contains(item.getName())) {
                workspaceSystemFields.add(item);
            }
        });
        return workspaceSystemFields;
    }

    private List<CustomField> getGlobalField(String scene) {
        return queryChain().where(CUSTOM_FIELD.GLOBAL.eq(true).and(CUSTOM_FIELD.SCENE.eq(scene))).list();
    }

    private QueryChain<CustomField> queryChain(QueryCustomFieldRequest page) {
        return queryChain().select(CUSTOM_FIELD.ALL_COLUMNS).from(CUSTOM_FIELD.as("cf"))
                .where(CUSTOM_FIELD.NAME.like(page.getName()))
                .and(CUSTOM_FIELD.WORKSPACE_ID.eq(page.getWorkspaceId())
                        .or(CUSTOM_FIELD.GLOBAL.eq(true).and(notExists(
                                QueryChain.of(CustomField.class)
                                        .select(CUSTOM_FIELD.ID)
                                        .from(CUSTOM_FIELD.as("cf_child"))
                                        .where("cf_child.name = cf.name").and("cf_child.scene = cf.scene")
                                        .and("cf_child.global != 1")
                                        .and("cf_child.workspace_id=" + page.getWorkspaceId())
                        ))).when(StringUtils.isNoneBlank(page.getWorkspaceId())))
                // project
                .and(CUSTOM_FIELD.PROJECT_ID.eq(page.getProjectId())
                        .or(CUSTOM_FIELD.GLOBAL.eq(true).and(notExists(
                                QueryChain.of(CustomField.class).select(CUSTOM_FIELD.ID).from(CUSTOM_FIELD.as("cf_child"))
                                        .where("cf_child.name = cf.name").and("cf_child.scene = cf.scene")
                                        .and("cf_child.global != 1")
                                        .and("cf_child.project_id=" + page.getProjectId())
                        ))).when(StringUtils.isNoneBlank(page.getProjectId())))
                // ids
                .and(CUSTOM_FIELD.ID.in(page.getIds()))
                // templateContainIds
                .and(CUSTOM_FIELD.ID.notIn(page.getTemplateId()));
    }

    private void checkExist(CustomField customField) {
        if (Objects.nonNull(customField.getName())) {
            boolean exists = queryChain().where(CUSTOM_FIELD.NAME.eq(StringUtils.trim(customField.getName()))
                    .and(CUSTOM_FIELD.PROJECT_ID.eq(customField.getProjectId()))
                    .and(CUSTOM_FIELD.ID.ne(customField.getId()))).exists();
            if (exists) {
                BusinessException.throwException("工作空间下已存在该字段：" + customField.getName());
            }
        }
    }
}
