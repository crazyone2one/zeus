package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.CustomFieldType;
import cn.master.zeus.common.constants.TemplateConstants;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.TestCaseTemplateDao;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.UpdateCaseFieldTemplateRequest;
import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.TestCaseTemplate;
import cn.master.zeus.mapper.TestCaseTemplateMapper;
import cn.master.zeus.service.ICustomFieldService;
import cn.master.zeus.service.ICustomFieldTemplateService;
import cn.master.zeus.service.ITestCaseTemplateService;
import cn.master.zeus.service.TemplateBaseService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.TestCaseTemplateTableDef.TEST_CASE_TEMPLATE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestCaseTemplateServiceImpl extends ServiceImpl<TestCaseTemplateMapper, TestCaseTemplate> implements ITestCaseTemplateService {

    private final ICustomFieldTemplateService customFieldTemplateService;
    private final TemplateBaseService templateBaseService;
    private final ICustomFieldService customFieldService;

    @Override
    public Page<TestCaseTemplate> getPage(BaseRequest request) {
        QueryChain<TestCaseTemplate> queryChain = queryChain().select(TEST_CASE_TEMPLATE.ALL_COLUMNS).from(TEST_CASE_TEMPLATE.as("tcft"))
                .where(TEST_CASE_TEMPLATE.NAME.like(request.getName()))
                .orderBy(TEST_CASE_TEMPLATE.UPDATE_TIME.desc());
        return mapper.paginate(Page.of(request.getPageNumber(), request.getPageSize()), queryChain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(UpdateCaseFieldTemplateRequest request) {
        checkExist(request);
        TestCaseTemplate testCaseTemplate = new TestCaseTemplate();
        BeanUtils.copyProperties(request, testCaseTemplate);
        testCaseTemplate.setGlobal(false);
        testCaseTemplate.setCreateUser(SessionUtils.getUserId());
        if (Objects.isNull(testCaseTemplate.getSystem())) {
            testCaseTemplate.setSystem(false);
        }
        mapper.insert(testCaseTemplate);
        customFieldTemplateService.create(request.getCustomFields(), testCaseTemplate.getId(), TemplateConstants.FieldTemplateScene.TEST_CASE.name());
        return testCaseTemplate.getId();
    }

    private void checkExist(UpdateCaseFieldTemplateRequest request) {
        if (Objects.nonNull(request.getName())) {
            boolean exists = queryChain().where(TEST_CASE_TEMPLATE.NAME.eq(request.getName()).and(TEST_CASE_TEMPLATE.PROJECT_ID.eq(request.getProjectId()))
                    .and(TEST_CASE_TEMPLATE.ID.ne(request.getId()))).exists();
            if (exists) {
                BusinessException.throwException("工作空间下已存在该模板" + request.getName());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTestCaseTemplate(UpdateCaseFieldTemplateRequest request) {
        if (request.getGlobal() != null && request.getGlobal()) {
            String originId = request.getId();
            // 如果是全局字段，则创建对应工作空间字段
            String id = add(request);
            // 把原来为系统模板的项目模板设置成新的模板
            UpdateChain.of(Project.class).set(Project::getCaseTemplateId, id)
                    .where(PROJECT.CASE_TEMPLATE_ID.eq(originId).or(PROJECT.CASE_TEMPLATE_ID.isNull()).or(PROJECT.CASE_TEMPLATE_ID.eq("")))
                    .and(PROJECT.ID.eq(request.getProjectId()));
        } else {
            checkExist(request);
            customFieldTemplateService.deleteByTemplateId(request.getId());
            TestCaseTemplate testCaseTemplate = new TestCaseTemplate();
            BeanUtils.copyProperties(request, testCaseTemplate);
            mapper.update(testCaseTemplate);
            customFieldTemplateService.create(request.getCustomFields(), testCaseTemplate.getId(), TemplateConstants.FieldTemplateScene.TEST_CASE.name());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        templateBaseService.checkTemplateUsed(id);
        mapper.deleteById(id);
        customFieldTemplateService.deleteByTemplateId(id);
    }

    @Override
    public List<TestCaseTemplate> getOption(String projectId) {
        List<TestCaseTemplate> testCaseTemplates;
        if (StringUtils.isBlank(projectId)) {
            return queryChain().where(TEST_CASE_TEMPLATE.GLOBAL.eq(true).and(TEST_CASE_TEMPLATE.SYSTEM.eq(true))).list();
        }
        testCaseTemplates = queryChain().where(TEST_CASE_TEMPLATE.PROJECT_ID.eq(projectId).or(TEST_CASE_TEMPLATE.SYSTEM.eq(true))).list();
        testCaseTemplates.add(getDefaultTemplate(projectId));
        return testCaseTemplates;
    }

    @Override
    public TestCaseTemplateDao getTemplate(String projectId) {
        Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(projectId)).one();
        String caseTemplateId = project.getCaseTemplateId();
        TestCaseTemplate caseTemplate;
        TestCaseTemplateDao caseTemplateDao = new TestCaseTemplateDao();
        if (StringUtils.isNoneBlank(caseTemplateId)) {
            caseTemplate = mapper.selectOneById(caseTemplateId);
            if (Objects.isNull(caseTemplate)) {
                caseTemplate = getDefaultTemplate(projectId);
            }
        } else {
            caseTemplate = getDefaultTemplate(projectId);
        }
        BeanUtils.copyProperties(caseTemplate, caseTemplateDao);
        List<CustomFieldDao> result = customFieldService.getCustomFieldByTemplateId(caseTemplate.getId());
        caseTemplateDao.setCustomFields(result);
        return caseTemplateDao;
    }

    @Override
    public void handleSystemFieldCreate(CustomFieldDao customFieldDao) {
        List<TestCaseTemplate> testCaseTemplates = queryChain().where(TEST_CASE_TEMPLATE.GLOBAL.eq(true).or(TEST_CASE_TEMPLATE.PROJECT_ID.eq(customFieldDao.getProjectId()))).list();
        Map<Boolean, List<TestCaseTemplate>> templatesMap = testCaseTemplates.stream().collect(Collectors.groupingBy(TestCaseTemplate::getGlobal));
        // 获取全局模板
        List<TestCaseTemplate> globalTemplates = templatesMap.get(true);
        // 获取当前工作空间下模板
        List<TestCaseTemplate> projectTemplates = templatesMap.get(false);
        globalTemplates.forEach(g->{
            List<TestCaseTemplate> projectTemplate = null;
            if (CollectionUtils.isNotEmpty(projectTemplates)) {
                projectTemplate = projectTemplates.stream().filter(p -> p.getName().equals(g.getName())).toList();
            }
            // 如果没有项目级别的模板就创建
            if (CollectionUtils.isEmpty(projectTemplate)) {
                TestCaseTemplate template = new TestCaseTemplate();
                BeanUtils.copyProperties(g, template);
                template.setCreateUser(SessionUtils.getUserId());
                template.setGlobal(false);
                template.setProjectId(customFieldDao.getProjectId());
                mapper.insert(template);
                UpdateChain.of(Project.class).set(Project::getCaseTemplateId, template.getId())
                        .where(PROJECT.CASE_TEMPLATE_ID.eq(g.getId()).or(PROJECT.CASE_TEMPLATE_ID.isNull()).or(PROJECT.CASE_TEMPLATE_ID.eq("")))
                        .and(PROJECT.ID.eq(customFieldDao.getProjectId()));
                List<CustomFieldTemplate> customFieldTemplate = customFieldTemplateService.getSystemFieldCreateTemplate(customFieldDao, g.getId());
                customFieldTemplateService.create(customFieldTemplate, template.getId(), TemplateConstants.FieldTemplateScene.TEST_CASE.name());
            }
        });
    }

    @Override
    public TestCaseTemplateDao getTemplateForList(String projectId) {
        TestCaseTemplateDao template = getTemplate(projectId);
        // 列表展示过滤掉文本框和富文本框等大字段，否则加载效率低
        List<CustomFieldDao> fields = template.getCustomFields().stream()
                .filter(field -> !StringUtils.equalsAnyIgnoreCase(field.getType(),
                        CustomFieldType.TEXTAREA.getValue(), CustomFieldType.RICH_TEXT.getValue()))
                .collect(Collectors.toList());
        template.setCustomFields(fields);
        return template;
    }

    private TestCaseTemplate getDefaultTemplate(String projectId) {
        List<TestCaseTemplate> list = queryChain().where(TEST_CASE_TEMPLATE.PROJECT_ID.eq(projectId).or(TEST_CASE_TEMPLATE.SYSTEM.eq(true))).list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return queryChain().where(TEST_CASE_TEMPLATE.GLOBAL.eq(true)).list().get(0);
        }
    }
}
