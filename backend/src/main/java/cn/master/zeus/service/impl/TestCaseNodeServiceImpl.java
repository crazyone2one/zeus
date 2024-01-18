package cn.master.zeus.service.impl;

import cn.master.zeus.common.enums.ProjectModuleDefaultNodeEnum;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import cn.master.zeus.entity.TestCase;
import cn.master.zeus.entity.TestCaseNode;
import cn.master.zeus.entity.TestCaseReviewTestCase;
import cn.master.zeus.entity.TestPlanTestCase;
import cn.master.zeus.mapper.TestCaseMapper;
import cn.master.zeus.mapper.TestCaseNodeMapper;
import cn.master.zeus.service.BuildTreeService;
import cn.master.zeus.service.ITestCaseNodeService;
import cn.master.zeus.util.DateUtils;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.TestCaseNodeTableDef.TEST_CASE_NODE;
import static cn.master.zeus.entity.table.TestCaseReviewTestCaseTableDef.TEST_CASE_REVIEW_TEST_CASE;
import static cn.master.zeus.entity.table.TestCaseTableDef.TEST_CASE;
import static cn.master.zeus.entity.table.TestPlanTestCaseTableDef.TEST_PLAN_TEST_CASE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestCaseNodeServiceImpl extends ServiceImpl<TestCaseNodeMapper, TestCaseNode> implements ITestCaseNodeService {

    private final BuildTreeService buildTreeService;
    private final TestCaseMapper testCaseMapper;

    @Override
    public List<TestCaseNode> getNodeTreeByProjectId(String projectId) {
        QueryWrapper qw = QueryWrapper.create()
                .where(TEST_CASE_NODE.PROJECT_ID.eq(projectId).and(TEST_CASE_NODE.PARENT_ID.isNull()))
                .orderBy(TEST_CASE_NODE.POS.asc());
        return mapper.selectListWithRelationsByQuery(qw);
    }

    @Override
    public List<TestCaseNode> getNodeTreeByProjectId(String projectId, QueryTestCaseRequest request) {
        setRequestWeekParam(request);
        // 判断当前项目下是否有默认模块，没有添加默认模块
        checkDefaultNode(projectId);
        List<TestCaseNode> countNodes = getCountNodes(request);
        QueryWrapper qw = QueryWrapper.create()
                .where(TEST_CASE_NODE.PROJECT_ID.eq(projectId).and(TEST_CASE_NODE.PARENT_ID.isNull()))
                .orderBy(TEST_CASE_NODE.POS.asc());
        // 默认的递归查询深度为 3 个层级,设置递归查询深度为 10 层
        RelationManager.setMaxDepth(10);
        List<TestCaseNode> testCaseNodes = mapper.selectListWithRelationsByQuery(qw);
        return buildTreeService.getTestCaseNodeTrees(testCaseNodes, buildTreeService.getCountMap(countNodes));
    }

    private List<TestCaseNode> getCountNodes(QueryTestCaseRequest request) {
        QueryChain<TestCaseNode> caseNum = queryChain().select(TEST_CASE_NODE.ID, QueryMethods.count("*").as("caseNum"))
                .from(TEST_CASE)
                .leftJoin(TEST_CASE_NODE).on(TEST_CASE_NODE.ID.eq(TEST_CASE.NODE_ID))
                .where(TEST_CASE.ID.notIn(request.getTestCaseContainIds())
                        .and(TEST_CASE.NAME.like(request.getName())
                                .or(TEST_CASE.NUM.like(request.getName()))
                                .or(TEST_CASE.TAGS.eq(request.getName()))
                                .or(TEST_CASE.CUSTOM_NUM.like(request.getName())))
                        .and(TEST_CASE.PROJECT_ID.eq(request.getProjectId()))
                        .and(TEST_CASE.NODE_ID.in(request.getNodeId())))
                .groupBy(TEST_CASE_NODE.ID);
        return mapper.selectListByQuery(caseNum);
    }

    @Override
    public List<TestCaseNode> getRelationshipNodeByCondition(String projectId, QueryTestCaseRequest request) {
        return getNodeTreeByProjectId(projectId, request);
    }

    @Override
    public Map<String, Integer> getNodeCountMapByProjectId(String projectId, QueryTestCaseRequest request) {
        setRequestWeekParam(request);
        request.setProjectId(projectId);
        request.setUserId(SessionUtils.getUserId());
        request.setNodeIds(null);
        return null;
    }

    @Override
    public TestCaseNode checkDefaultNode(String projectId) {
        TestCaseNode defaultNode = getDefaultNode(projectId);
        if (defaultNode == null) {
            return createDefaultNode(projectId);
        } else {
            return defaultNode;
        }
    }

    @Override
    public Page<TestCaseNode> pageNode(QueryTestCaseRequest request) {
        // 判断当前项目下是否有默认模块，没有添加默认模块
        checkDefaultNode(request.getProjectId());
        QueryWrapper qw = QueryWrapper.create().from(TestCaseNode.class)
                .where(TEST_CASE_NODE.PROJECT_ID.eq(request.getProjectId()).and(TEST_CASE_NODE.PARENT_ID.isNull()))
                .orderBy(TEST_CASE_NODE.POS.asc());
        // 默认的递归查询深度为 3 个层级,设置递归查询深度为 10 层
        RelationManager.setMaxDepth(10);
        return mapper.paginateWithRelations(Page.of(request.getPageNumber(), request.getPageSize()), qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addNode(TestCaseNode node) {
        validateNode(node);
        node.setCreateUser(SessionUtils.getUserId());
        double pos = getNextLevelPos(node.getProjectId(), node.getLevel(), node.getParentId());
        node.setPos(pos);
        node.setParentId(StringUtils.isBlank(node.getParentId()) ? null : node.getParentId());
        mapper.insert(node);
        return node.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNode(List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return 1;
        }
        List<String> testCaseIdList = selectCaseIdByNodeIds(nodeIds);
        deleteTestCaseToGcBatch(testCaseIdList);
        QueryChain<TestCaseNode> queryChain = queryChain().where(TEST_CASE_NODE.ID.in(nodeIds));
        return mapper.deleteByQuery(queryChain);
    }

    private void deleteTestCaseToGcBatch(List<String> ids) {
        // test-plan-test-case
        UpdateChain.of(TestPlanTestCase.class).set(TestPlanTestCase::getIsDel, true).where(TEST_PLAN_TEST_CASE.ID.in(ids)).update();
        // test_case_review_test_case
        UpdateChain.of(TestCaseReviewTestCase.class).set(TestCaseReviewTestCase::getIsDel, true).where(TEST_CASE_REVIEW_TEST_CASE.ID.in(ids)).update();
        // test case
        UpdateChain.of(TestCase.class).set(TestCase::getStatus, "Trash")
                .set(TestCase::getDeleteTime, LocalDateTime.now())
                .set(TestCase::getDeleteUserId, SessionUtils.getUserId())
                .where(TEST_CASE.REF_ID.in(QueryChain.of(TestCase.class).select(TEST_CASE.REF_ID)))
                .update();
    }

    private List<String> selectCaseIdByNodeIds(List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return new ArrayList<>();
        } else {
            QueryChain<TestCase> queryChain = QueryChain.of(TestCase.class).select(TEST_CASE.ID)
                    .where(TEST_CASE.NODE_ID.in(nodeIds));
            return testCaseMapper.selectListByQuery(queryChain).stream().map(TestCase::getId).collect(Collectors.toList());
        }
    }

    private double getNextLevelPos(String projectId, Integer level, String parentId) {
        List<TestCaseNode> list = getPos(projectId, level, parentId);
        if (!CollectionUtils.isEmpty(list) && list.get(0) != null && list.get(0).getPos() != null) {
            return list.get(0).getPos() + 65536;
        } else {
            return 65536;
        }
    }

    private List<TestCaseNode> getPos(String projectId, Integer level, String parentId) {
        return queryChain().where(TEST_CASE_NODE.PROJECT_ID.eq(projectId)
                        .and(TEST_CASE_NODE.LEVEL.eq(level))
                        .and(TEST_CASE_NODE.PARENT_ID.eq(parentId).when(level != 1 && StringUtils.isNotBlank(parentId))))
                .list();
    }

    private void validateNode(TestCaseNode node) {
        checkTestCaseNodeExist(node);
    }

    private void checkTestCaseNodeExist(TestCaseNode node) {
        if (Objects.nonNull(node.getName())) {
            QueryChain<TestCaseNode> queryChain = queryChain()
                    .where(TEST_CASE_NODE.NAME.eq(node.getName())
                            .and(TEST_CASE_NODE.PROJECT_ID.eq(node.getProjectId())))
                    .and(TEST_CASE_NODE.ID.ne(node.getId()));
            if (StringUtils.isNotBlank(node.getParentId())) {
                queryChain.and(TEST_CASE_NODE.PARENT_ID.eq(node.getParentId()));
            } else {
                queryChain.and(TEST_CASE_NODE.LEVEL.eq(node.getLevel()));
            }
            if (queryChain.exists()) {
                BusinessException.throwException("同层级下已存在该模块名称");
            }
        }
    }

    private TestCaseNode createDefaultNode(String projectId) {
        // 双重检查, 判断是否已经存在默认节点
        if (getDefaultNode(projectId) != null) {
            return null;
        }
        TestCaseNode build = TestCaseNode.builder().createUser(SessionUtils.getUserId())
                .name(ProjectModuleDefaultNodeEnum.TEST_CASE_DEFAULT_NODE.getNodeName())
                .pos(1.0).level(1).projectId(projectId).build();
        mapper.insert(build);
        build.setCaseNum(0);
        return build;
    }

    private TestCaseNode getDefaultNode(String projectId) {
        List<TestCaseNode> defaultNodes = queryChain().where(TEST_CASE_NODE.PROJECT_ID.eq(projectId)
                .and(TEST_CASE_NODE.NAME.eq(ProjectModuleDefaultNodeEnum.TEST_CASE_DEFAULT_NODE.getNodeName()))
                .and(TEST_CASE_NODE.PARENT_ID.isNull())).list();
        if (CollectionUtils.isEmpty(defaultNodes)) {
            return null;
        } else {
            return defaultNodes.get(0);
        }
    }

    private void setRequestWeekParam(QueryTestCaseRequest request) {
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
