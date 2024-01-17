package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.CommonConstants;
import cn.master.zeus.common.enums.ProjectApplicationType;
import cn.master.zeus.common.enums.TestCaseReviewStatus;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.CustomFieldResourceDTO;
import cn.master.zeus.dto.ProjectConfig;
import cn.master.zeus.dto.TestCaseDTO;
import cn.master.zeus.dto.request.testcase.EditTestCaseRequest;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import cn.master.zeus.entity.*;
import cn.master.zeus.mapper.TestCaseFollowMapper;
import cn.master.zeus.mapper.TestCaseMapper;
import cn.master.zeus.service.ICustomFieldTestCaseService;
import cn.master.zeus.service.IProjectApplicationService;
import cn.master.zeus.service.IProjectVersionService;
import cn.master.zeus.service.ITestCaseService;
import cn.master.zeus.util.DateUtils;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ApiScenarioTableDef.API_SCENARIO;
import static cn.master.zeus.entity.table.ApiTestCaseTableDef.API_TEST_CASE;
import static cn.master.zeus.entity.table.LoadTestTableDef.LOAD_TEST;
import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.ProjectVersionTableDef.PROJECT_VERSION;
import static cn.master.zeus.entity.table.TestCaseFollowTableDef.TEST_CASE_FOLLOW;
import static cn.master.zeus.entity.table.TestCaseNodeTableDef.TEST_CASE_NODE;
import static cn.master.zeus.entity.table.TestCaseTableDef.TEST_CASE;
import static cn.master.zeus.entity.table.TestCaseTestTableDef.TEST_CASE_TEST;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements ITestCaseService {

    private final IProjectApplicationService projectApplicationService;
    private final IProjectVersionService projectVersionService;
    private final TestCaseFollowMapper testCaseFollowMapper;
    private final ICustomFieldTestCaseService customFieldTestCaseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestCase add(EditTestCaseRequest request) {
        final TestCase testCaseWithBlobs = addTestCase(request);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestCase addTestCase(EditTestCaseRequest request) {
        checkTestCaseExist(request, false);
        checkTestCustomNum(request);
        request.setNum(getNextNum(request.getProjectId()));
        if (StringUtils.isBlank(request.getCustomNum())) {
            request.setCustomNum(request.getNum().toString());
        }
        request.setReviewStatus(TestCaseReviewStatus.Prepare.name());
        request.setCreateUser(SessionUtils.getUserId());
        request.setLastExecuteResult(null);
        setNode(request);
        request.setOrder(getNextOrder(request.getProjectId()));
        //直接点保存 || 复制走的逻辑
        if (StringUtils.isAllBlank(request.getRefId(), request.getVersionId())) {
            //新创建测试用例，默认使用最新版本
            request.setRefId(request.getId());
            request.setVersionId(projectVersionService.getDefaultVersion(request.getProjectId()));
        } else if (StringUtils.isNotBlank(request.getVersionId())) {
            //从版本选择直接创建
            request.setRefId(request.getId());
        }
        //完全新增一条记录直接就是最新
        request.setLatest(true);
        // todo 同步用例与需求的关联关系
        //addDemandHyperLink(request, "add");
        mapper.insert(request);
        saveFollows(request.getId(), request.getFollows());
        List<CustomFieldResourceDTO> addFields = request.getAddFields();
        if (CollectionUtils.isNotEmpty(addFields)) {
            addFields.addAll(request.getEditFields());
            customFieldTestCaseService.addFields(request.getId(), addFields);
        }
        return request;
    }

    private Long getNextOrder(String projectId) {
        TestCase testCase = queryChain().select(TEST_CASE.ORDER)
                .where(TEST_CASE.PROJECT_ID.eq(projectId)).orderBy(TEST_CASE.ORDER.desc()).limit(1).one();
        Long lastOrder = testCase.getOrder();
        return (lastOrder == null ? 0 : lastOrder) + 5000;
    }

    private void setNode(EditTestCaseRequest testCase) {
        if (StringUtils.isEmpty(testCase.getNodeId()) || "default-module".equals(testCase.getNodeId())) {
            List<TestCaseNode> nodes = QueryChain.of(TestCaseNode.class).where(TEST_CASE_NODE.PROJECT_ID.eq(testCase.getProjectId())
                    .and(TEST_CASE_NODE.NAME.eq("未规划用例"))).list();
            if (CollectionUtils.isNotEmpty(nodes)) {
                testCase.setNodeId(nodes.get(0).getId());
            }
        }
    }

    @Override
    public int getNextNum(String projectId) {
        TestCase testCase = getMaxNumByProjectId(projectId);
        if (Objects.isNull(testCase) || Objects.isNull(testCase.getNum())) {
            return 100001;
        } else {
            return Optional.of(testCase.getNum() + 1).orElse(100001);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFollows(String caseId, List<String> follows) {

        if (CollectionUtils.isNotEmpty(follows)) {
            QueryChain<TestCaseFollow> queryChain = QueryChain.of(TestCaseFollow.class).where(TEST_CASE_FOLLOW.CASE_ID.eq(caseId));
            testCaseFollowMapper.deleteByQuery(queryChain);
            follows.forEach(follow -> {
                TestCaseFollow build = TestCaseFollow.builder().caseId(caseId).followId(follow).build();
                testCaseFollowMapper.insert(build);
            });
        }
    }

    @Override
    public List<String> getFollows(String caseId) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(caseId)) {
            return result;
        }
        List<TestCaseFollow> follows = QueryChain.of(TestCaseFollow.class).where(TEST_CASE_FOLLOW.CASE_ID.eq(caseId)).list();
        return follows.stream().map(TestCaseFollow::getFollowId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<TestCaseDTO> getTestCaseVersions(String caseId) {
        TestCase testCase = mapper.selectOneById(caseId);
        if (Objects.isNull(testCase)) {
            return new ArrayList<>();
        }
        QueryTestCaseRequest request = new QueryTestCaseRequest();
        request.setRefId(testCase.getRefId());
        if (CommonConstants.TRASH_STATUS.equalsIgnoreCase(testCase.getStatus())) {
            request.setFilters(new HashMap<>() {
                @Serial
                private static final long serialVersionUID = 7656278760319683749L;

                {
                    put("status", new ArrayList() {
                        @Serial
                        private static final long serialVersionUID = 2599911267062544334L;

                        {
                            add(CommonConstants.TRASH_STATUS);
                        }
                    });
                }
            });
        }
        return listTestCase(request);
    }

    @Override
    public List<TestCaseDTO> listTestCase(QueryTestCaseRequest request) {
        return listTestCase(request, false);
    }

    /**
     * 构造测试用例数据查询条件
     *
     * @param request 查询参数
     * @return com.mybatisflex.core.query.QueryChain<cn.master.zeus.entity.TestCase>
     */
    private QueryChain<TestCase> queryChain(QueryTestCaseRequest request) {
        return queryChain().select(TEST_CASE.ALL_COLUMNS, PROJECT_VERSION.NAME.as("versionName"), PROJECT_VERSION.ID.as("versionId"))
                .from(TEST_CASE).leftJoin(PROJECT_VERSION).on(PROJECT_VERSION.ID.eq(TEST_CASE.VERSION_ID))
                .where(TEST_CASE.STATUS.ne(request.getStatusIsNot())
                        .and(TEST_CASE.STATUS.ne(request.getNotEqStatus()))
                        .and(TEST_CASE.CASE_PUBLIC.eq(true))
                        .and(TEST_CASE.NAME.like(request.getName())
                                .or(TEST_CASE.NUM.like(request.getName()))
                                .or(TEST_CASE.TAGS.like(request.getName()))
                                .or(TEST_CASE.CUSTOM_NUM.like(request.getName())))
                        .and(TEST_CASE.ID.in(request.getIds()))
                        .and(TEST_CASE.ID.notIn(request.getNotInIds()))
                        .and(TEST_CASE.ID.in(
                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID).from(TEST_CASE_TEST).where(TEST_CASE_TEST.CREATE_TIME.ge(request.getCreateTime()))
                        ).when(Objects.nonNull(request.getRelevanceCreateTime())))
                        .and(TEST_CASE.CREATE_TIME.ge(request.getCreateTime()))
                        .and(TEST_CASE.NODE_ID.in(request.getNodeIds()))
                        .and(TEST_CASE.PROJECT_ID.eq(request.getProjectId()))
                        .and(TEST_CASE.ID.notIn(
                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID).from(TEST_CASE_TEST)
                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("testCase")
                                                .and(TEST_CASE_TEST.TEST_ID.in(
                                                        QueryChain.of(ApiTestCase.class).select(API_TEST_CASE.ID)
                                                                .from(ApiTestCase.class)
                                                                .where(API_TEST_CASE.STATUS.isNull().or(API_TEST_CASE.STATUS.ne("Trash")))
                                                )))
                                        .union(
                                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID)
                                                        .from(TEST_CASE_TEST)
                                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("performance")
                                                                .and(TEST_CASE_TEST.TEST_ID.in(QueryChain.of(LoadTest.class).select(LOAD_TEST.ID))))
                                        ).union(
                                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID)
                                                        .from(TEST_CASE_TEST)
                                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("automation")
                                                                .and(TEST_CASE_TEST.TEST_ID.in(
                                                                        QueryChain.of(ApiScenario.class).select(LOAD_TEST.ID).where(API_SCENARIO.STATUS.ne("Trash")))))
                                        )
                        ).when(StringUtils.equals(request.getCaseCoverage(), "uncoverage")))
                        .and(TEST_CASE.ID.in(
                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID).from(TEST_CASE_TEST)
                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("testCase")
                                                .and(TEST_CASE_TEST.TEST_ID.in(
                                                        QueryChain.of(ApiTestCase.class).select(API_TEST_CASE.ID)
                                                                .from(ApiTestCase.class)
                                                                .where(API_TEST_CASE.STATUS.isNull().or(API_TEST_CASE.STATUS.ne("Trash")))
                                                )))
                                        .union(
                                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID)
                                                        .from(TEST_CASE_TEST)
                                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("performance")
                                                                .and(TEST_CASE_TEST.TEST_ID.in(QueryChain.of(LoadTest.class).select(LOAD_TEST.ID))))
                                        ).union(
                                                QueryChain.of(TestCaseTest.class).select(TEST_CASE_TEST.TEST_CASE_ID)
                                                        .from(TEST_CASE_TEST)
                                                        .where(TEST_CASE_TEST.TEST_TYPE.eq("automation")
                                                                .and(TEST_CASE_TEST.TEST_ID.in(QueryChain.of(ApiScenario.class).select(LOAD_TEST.ID).where(API_SCENARIO.STATUS.ne("Trash")))))
                                        )
                        ).when(StringUtils.equals(request.getCaseCoverage(), "coverage")))
                );
    }

    @Override
    public List<TestCaseDTO> listTestCase(QueryTestCaseRequest request, boolean isSampleInfo) {
        initRequest(request, true);
        setDefaultOrder(request);
        if (request.getFilters() != null && !request.getFilters().containsKey("status")) {
            request.getFilters().put("status", new ArrayList<>(0));
        }
        QueryChain<TestCase> queryChain = queryChain(request);
        List<TestCaseDTO> list = mapper.selectListByQueryAs(queryChain, TestCaseDTO.class);
        if (!isSampleInfo) {
            buildUserInfoByCurrentProjectMembers(list);
            //if (StringUtils.isNotBlank(request.getProjectId())) {
            //    buildProjectInfo(request.getProjectId(), list);
            //} else {
            //    buildProjectInfoWithoutProject(list);
            //}
            buildCustomField(list);
        }
        return list;
    }

    @Override
    public Page<TestCaseDTO> pageTestCase(QueryTestCaseRequest request) {
        QueryChain<TestCase> queryChain = queryChain(request);
        return mapper.paginateAs(Page.of(request.getPageNumber(), request.getPageSize()), queryChain, TestCaseDTO.class);
    }

    private void buildCustomField(List<TestCaseDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Map<String, List<CustomFieldDao>> fieldMap = customFieldTestCaseService.getMapByResourceIdsForList(list.stream().map(TestCaseDTO::getId).collect(Collectors.toList()));
        list.forEach(i -> i.setFields(fieldMap.get(i.getId())));
    }

    private void buildUserInfoByCurrentProjectMembers(List<TestCaseDTO> list) {
        // todo build User Info
    }

    private void setDefaultOrder(QueryTestCaseRequest request) {

    }

    private void initRequest(QueryTestCaseRequest request, boolean checkThisWeekData) {
        if (checkThisWeekData) {
            Map<String, LocalDateTime> weekFirstTimeAndLastTime = DateUtils.getWeedFirstTimeAndLastTime(LocalDateTime.now());
            LocalDateTime weekFirstTime = weekFirstTimeAndLastTime.get("firstTime");
            if (request.isSelectThisWeedData()) {
                if (weekFirstTime != null) {
                    request.setCreateTime(weekFirstTime);
                }
            }
            if (request.isSelectThisWeedRelevanceData()) {
                if (weekFirstTime != null) {
                    request.setRelevanceCreateTime(weekFirstTime);
                }
            }
        }
    }

    private TestCase getMaxNumByProjectId(String projectId) {
        return queryChain().where(TEST_CASE.PROJECT_ID.eq(projectId)).orderBy(TEST_CASE.NUM.desc()).limit(1).one();
    }

    private void checkTestCustomNum(EditTestCaseRequest testCase) {
        if (StringUtils.isNotBlank(testCase.getCustomNum())) {
            Project project = QueryChain.of(Project.class).where(PROJECT.ID.eq(testCase.getProjectId())).one();
            if (Objects.nonNull(project)) {
                ProjectConfig config = projectApplicationService.getSpecificTypeValue(project.getId(), ProjectApplicationType.CASE_CUSTOM_NUM.name());
                boolean customNum = config.getCaseCustomNum();
                // 未开启自定义ID
                if (!customNum) {
                    testCase.setCustomNum(null);
                } else {
                    checkCustomNumExist(testCase);
                }
            } else {
                BusinessException.throwException("add test case fail, project is not find.");
            }
        }
    }

    private void checkCustomNumExist(EditTestCaseRequest testCase) {
        String id = testCase.getId();
        TestCase testCaseWithBlob = mapper.selectOneById(id);
        List<TestCase> list = queryChain().where(TEST_CASE.CUSTOM_NUM.eq(testCase.getCustomNum())
                        .and(TEST_CASE.PROJECT_ID.eq(testCase.getProjectId()))
                        .and(TEST_CASE.STATUS.ne(CommonConstants.TRASH_STATUS))
                        .and(TEST_CASE.ID.ne(testCase.getId())))
                .and(TEST_CASE.REF_ID.ne(testCaseWithBlob.getRefId())
                        .when(StringUtils.isNotBlank(testCaseWithBlob.getRefId()))).list();
        if (CollectionUtils.isNotEmpty(list)) {
            BusinessException.throwException("用例自定义ID已存在");
        }
    }

    private void checkTestCaseExist(EditTestCaseRequest testCase, boolean isEdit) {
        if (Objects.nonNull(testCase)) {
            // 检查是否已经存在
            List<TestCase> caseList = queryChain().where(TEST_CASE.NAME.eq(testCase.getName())
                            .and(TEST_CASE.PROJECT_ID.eq(testCase.getProjectId()))
                            .and(TEST_CASE.STATUS.ne(CommonConstants.TRASH_STATUS)))
                    .and(TEST_CASE.NODE_ID.eq(testCase.getNodeId()))
                    .and(TEST_CASE.PRIORITY.eq(testCase.getPriority()))
                    .and(TEST_CASE.TEST_ID.eq(testCase.getTestId()))
                    .and(TEST_CASE.VERSION_ID.eq(testCase.getVersionId()))
                    .and(TEST_CASE.ID.ne(testCase.getId())).list();
            // 如果上边字段全部相同，去检查 remark 和 steps
            if (CollectionUtils.isNotEmpty(caseList)) {
                String caseRemark = testCase.getRemark() == null ? StringUtils.EMPTY : testCase.getRemark();
                String caseSteps = testCase.getSteps() == null ? StringUtils.EMPTY : testCase.getSteps();
                String casePrerequisite = testCase.getPrerequisite() == null ? StringUtils.EMPTY : testCase.getPrerequisite();
                for (TestCase tc : caseList) {
                    String steps = tc.getSteps() == null ? StringUtils.EMPTY : tc.getSteps();
                    String remark = tc.getRemark() == null ? StringUtils.EMPTY : tc.getRemark();
                    String prerequisite = tc.getPrerequisite() == null ? StringUtils.EMPTY : tc.getPrerequisite();
                    if (StringUtils.equals(steps, caseSteps) && StringUtils.equals(remark, caseRemark) && StringUtils.equals(prerequisite, casePrerequisite)) {
                        BusinessException.throwException("该模块下已存在该测试用例");
                    }
                }
            }
        }
    }
}
