package cn.master.zeus.service.impl;

import cn.master.zeus.common.enums.ProjectModuleDefaultNodeEnum;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.plan.QueryTestPlanRequest;
import cn.master.zeus.entity.TestPlan;
import cn.master.zeus.entity.TestPlanNode;
import cn.master.zeus.mapper.TestPlanMapper;
import cn.master.zeus.mapper.TestPlanNodeMapper;
import cn.master.zeus.service.ITestPlanNodeService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.zeus.entity.table.ProjectTableDef.PROJECT;
import static cn.master.zeus.entity.table.ScheduleTableDef.SCHEDULE;
import static cn.master.zeus.entity.table.TestPlanNodeTableDef.TEST_PLAN_NODE;
import static cn.master.zeus.entity.table.TestPlanTableDef.TEST_PLAN;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestPlanNodeServiceImpl extends ServiceImpl<TestPlanNodeMapper, TestPlanNode> implements ITestPlanNodeService {

    private final TestPlanMapper testPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addNode(TestPlanNode node) {
        checkTestPlanNodeExist(node);
        node.setCreateUser(SessionUtils.getUserId());
        double pos = getNextLevelPos(node.getProjectId(), node.getLevel(), node.getParentId());
        node.setPos(pos);
        mapper.insert(node);
        return node.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNode(List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return 1;
        }
        // 删除所有节点下的计划
        testPlanMapper.deleteByQuery(QueryChain.of(TestPlan.class).where(TEST_PLAN.NODE_ID.in(nodeIds)));
        // 删除节点
        return mapper.deleteBatchByIds(nodeIds);
    }

    @Override
    public List<TestPlanNode> getNodeTree(String projectId, QueryTestPlanRequest request) {
        checkDefaultNode(projectId);
        QueryWrapper qw = QueryWrapper.create();
        qw.select(TEST_PLAN_NODE.NAME, TEST_PLAN_NODE.ID, TEST_PLAN_NODE.POS, TEST_PLAN_NODE.LEVEL)
                .where(TEST_PLAN_NODE.PROJECT_ID.eq(projectId).and(TEST_PLAN_NODE.LEVEL.eq(1)));
        return mapper.selectListWithRelationsByQuery(qw);
    }

    private List<TestPlanNode> getNodeTrees(List<TestPlanNode> nodes, Map<String, Integer> countMap) {
        List<TestPlanNode> nodeTreeList = new ArrayList<>();
        Map<Integer, List<TestPlanNode>> nodeLevelMap = new HashMap<>();
        nodes.forEach(node -> {
            Integer level = node.getLevel();
            if (nodeLevelMap.containsKey(level)) {
                nodeLevelMap.get(level).add(node);
            } else {
                List<TestPlanNode> testCaseNodes = new ArrayList<>();
                testCaseNodes.add(node);
                nodeLevelMap.put(node.getLevel(), testCaseNodes);
            }
        });
        List<TestPlanNode> rootNodes = Optional.ofNullable(nodeLevelMap.get(1)).orElse(new ArrayList<>());
        //rootNodes.forEach(rootNode -> nodeTreeList.add(buildNodeTree(nodeLevelMap, rootNode, countMap)));
        return nodeTreeList;
    }


    private List<TestPlanNode> getNodeTreeByProjectId(String projectId) {
        QueryChain<TestPlanNode> qw = queryChain().select(TEST_PLAN_NODE.ID, TEST_PLAN_NODE.PROJECT_ID, TEST_PLAN_NODE.NAME, TEST_PLAN_NODE.PARENT_ID, TEST_PLAN_NODE.LEVEL, TEST_PLAN_NODE.POS)
                .where(TEST_PLAN_NODE.PROJECT_ID.eq(projectId)).orderBy(TEST_PLAN_NODE.POS.desc());
        return mapper.selectListWithRelationsByQuery(qw);
    }

    private List<TestPlanNode> getCountNodes(QueryTestPlanRequest request) {
        QueryWrapper wrapper = QueryChain.create().select(TEST_PLAN_NODE.ID, QueryMethods.count("*").as("caseNum"))
                .from(TEST_PLAN)
                .leftJoin(TEST_PLAN_NODE).on(TEST_PLAN.NODE_ID.eq(TEST_PLAN_NODE.ID))
                .leftJoin(PROJECT).on(TEST_PLAN_NODE.PROJECT_ID.eq(PROJECT.ID))
                .leftJoin(SCHEDULE).on(SCHEDULE.RESOURCE_ID.eq(TEST_PLAN.ID))
                .where(TEST_PLAN.NAME.like(request.getName())
                        .and(TEST_PLAN.WORKSPACE_ID.eq(request.getWorkspaceId()))
                        .and(TEST_PLAN.PROJECT_ID.eq(request.getProjectId()))
                        .and(TEST_PLAN.ID.eq(request.getId()))
                        .and(TEST_PLAN.STATUS.ne("Archived")
                                .when(Objects.isNull(request.getFilters()) || request.getFilters().isEmpty() && request.isByFilter())))
                .groupBy(TEST_PLAN_NODE.ID);
        return mapper.selectListByQuery(wrapper);
    }

    @Override
    public void createDefaultNode(String projectId) {
        if (getDefaultNode(projectId) != null) {
            return;
        }
        TestPlanNode build = TestPlanNode.builder()
                .name(ProjectModuleDefaultNodeEnum.DEFAULT_NODE.getNodeName())
                .pos(1.0).level(1).projectId(projectId)
                .createUser(SessionUtils.getUserId())
                .build();
        mapper.insert(build);
    }

    @Override
    public Page<TestPlanNode> getPageData(BaseRequest page) {
        checkDefaultNode(page.getProjectId());
        QueryWrapper qw = QueryWrapper.create();
        qw.select(TEST_PLAN_NODE.ALL_COLUMNS)
                .where(TEST_PLAN_NODE.PROJECT_ID.eq(page.getProjectId()).and(TEST_PLAN_NODE.LEVEL.eq(1)));
        return mapper.paginateWithRelations(Page.of(page.getPageNumber(), page.getPageSize()), qw);
    }

    private void checkDefaultNode(String projectId) {
        TestPlanNode defaultNode = getDefaultNode(projectId);
        if (Objects.isNull(defaultNode)) {
            createDefaultNode(projectId);
        }
    }

    private TestPlanNode getDefaultNode(String projectId) {
        List<TestPlanNode> defaultNodes = queryChain().where(TEST_PLAN_NODE.PROJECT_ID.eq(projectId)
                .and(TEST_PLAN_NODE.NAME.eq(ProjectModuleDefaultNodeEnum.DEFAULT_NODE.getNodeName()))
                .and(TEST_PLAN_NODE.PARENT_ID.isNull())).list();
        if (CollectionUtils.isEmpty(defaultNodes)) {
            return null;
        } else {
            return defaultNodes.get(0);
        }
    }

    private double getNextLevelPos(String projectId, Integer level, String parentId) {
        List<TestPlanNode> list = getPos(projectId, level, parentId);
        if (!CollectionUtils.isEmpty(list) && list.get(0) != null && list.get(0).getPos() != null) {
            return list.get(0).getPos() + 65536;
        } else {
            return 65536;
        }
    }

    private List<TestPlanNode> getPos(String projectId, Integer level, String parentId) {
        return queryChain().where(TEST_PLAN_NODE.PROJECT_ID.eq(projectId)
                        .and(TEST_PLAN_NODE.LEVEL.eq(level)))
                .and(TEST_PLAN_NODE.PARENT_ID.eq(parentId).when(level != 1 && StringUtils.isNotBlank(parentId)))
                .orderBy(TEST_PLAN_NODE.POS.desc()).list();
    }

    private void checkTestPlanNodeExist(TestPlanNode node) {
        if (Objects.nonNull(node.getName())) {
            QueryChain<TestPlanNode> where = queryChain()
                    .where(TEST_PLAN_NODE.NAME.eq(node.getName())
                            .and(TEST_PLAN_NODE.PROJECT_ID.eq(node.getProjectId()))
                            .and(TEST_PLAN_NODE.ID.ne(node.getId())));
            if (StringUtils.isNotBlank(node.getParentId())) {
                where.and(TEST_PLAN_NODE.PARENT_ID.eq(node.getParentId()));
            } else {
                where.and(TEST_PLAN_NODE.LEVEL.eq(node.getLevel()));
            }
            if (where.exists()) {
                BusinessException.throwException("同层级下已存在该模块名称");
            }
        }
    }
}
