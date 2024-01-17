package cn.master.zeus.service.impl;

import cn.master.zeus.common.constants.TestPlanStatus;
import cn.master.zeus.common.exception.BusinessException;
import cn.master.zeus.dto.request.plan.AddTestPlanRequest;
import cn.master.zeus.entity.*;
import cn.master.zeus.mapper.*;
import cn.master.zeus.service.ITestPlanService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static cn.master.zeus.entity.table.TestPlanFollowTableDef.TEST_PLAN_FOLLOW;
import static cn.master.zeus.entity.table.TestPlanPrincipalTableDef.TEST_PLAN_PRINCIPAL;
import static cn.master.zeus.entity.table.TestPlanReportTableDef.TEST_PLAN_REPORT;
import static cn.master.zeus.entity.table.TestPlanTableDef.TEST_PLAN;
import static cn.master.zeus.entity.table.TestPlanTestCaseTableDef.TEST_PLAN_TEST_CASE;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements ITestPlanService {

    private final TestPlanPrincipalMapper testPlanPrincipalMapper;
    private final TestPlanFollowMapper testPlanFollowMapper;
    private final TestPlanReportMapper testPlanReportMapper;
    private final TestPlanTestCaseMapper testPlanTestCaseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestPlan addTestPlan(AddTestPlanRequest testPlan) {
        boolean exists = queryChain().where(TEST_PLAN.NAME.eq(testPlan.getName())
                .and(TEST_PLAN.PROJECT_ID.eq(SessionUtils.getCurrentProjectId()))).exists();
        if (exists) {
            BusinessException.throwException("测试计划名称已存在");
        }
        if (StringUtils.isBlank(testPlan.getProjectId())) {
            testPlan.setProjectId(SessionUtils.getCurrentProjectId());
        }
        testPlan.setStatus(TestPlanStatus.Prepare.name());
        testPlan.setCreator(SessionUtils.getUserId());
        mapper.insert(testPlan);
        List<String> principals = testPlan.getPrincipals();
        if (Objects.nonNull(principals) && !principals.isEmpty()) {
            principals.forEach(f -> {
                TestPlanPrincipal build = TestPlanPrincipal.builder().testPlanId(testPlan.getId()).principalId(f).build();
                testPlanPrincipalMapper.insert(build);
            });
        }
        List<String> follows = testPlan.getFollows();
        if (Objects.nonNull(follows) && !follows.isEmpty()) {
            follows.forEach(f -> {
                TestPlanFollow build = TestPlanFollow.builder().testPlanId(testPlan.getId()).followId(f).build();
                testPlanFollowMapper.insert(build);
            });
        }
        return testPlan;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestPlan editTestPlanWithRequest(AddTestPlanRequest request) {
        List<String> principals = request.getPrincipals();
        if (Objects.nonNull(principals) && !principals.isEmpty()) {
            if (StringUtils.isNotBlank(request.getId())) {
                QueryChain<TestPlanPrincipal> where = QueryChain.of(TestPlanPrincipal.class).where(TEST_PLAN_PRINCIPAL.TEST_PLAN_ID.eq(request.getId()));
                testPlanPrincipalMapper.deleteByQuery(where);
                principals.forEach(f -> {
                    TestPlanPrincipal build = TestPlanPrincipal.builder().testPlanId(request.getId()).principalId(f).build();
                    testPlanPrincipalMapper.insert(build);
                });
            }
        }
        return editTestPlan(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestPlan editTestPlan(TestPlan testPlan) {
        checkTestPlanExist(testPlan);
        TestPlan res = mapper.selectOneById(testPlan.getId());
        if (!res.getStatus().equals(testPlan.getStatus())) {    //  若有改变才更新时间
            if (TestPlanStatus.Underway.name().equals(testPlan.getStatus())) {
                if (res.getStatus().equals(TestPlanStatus.Prepare.name())) {
                    testPlan.setActualStartTime(System.currentTimeMillis());
                }   //  未开始->进行中，写入实际开始时间
                else if (res.getStatus().equals(TestPlanStatus.Completed.name())) {
                    testPlan.setActualEndTime(null);
                }   //  已完成->进行中，结束时间置空
            } else if (!res.getStatus().equals(TestPlanStatus.Prepare.name()) &&
                    TestPlanStatus.Prepare.name().equals(testPlan.getStatus())) {
                testPlan.setActualStartTime(null);
                testPlan.setActualEndTime(null);
            }   //  非未开始->未开始，时间都置空
            else if (TestPlanStatus.Completed.name().equals(testPlan.getStatus()) &&
                    !TestPlanStatus.Completed.name().equals(res.getStatus())) {
                //已完成，写入实际完成时间
                testPlan.setActualEndTime(System.currentTimeMillis());
            } else if (!res.getStatus().equals(TestPlanStatus.Finished.name()) &&
                    TestPlanStatus.Finished.name().equals(testPlan.getStatus())) {
                testPlan.setActualEndTime(System.currentTimeMillis());
            }   //  非已结束->已结束，更新结束时间
        }
        // 如果状态是未开始，设置时间为null
        if (StringUtils.isNotBlank(testPlan.getStatus()) && testPlan.getStatus().equals(TestPlanStatus.Prepare.name())) {
            testPlan.setActualStartTime(null);
            testPlan.setActualEndTime(null);
        }
        // 如果当前状态已完成，没有结束时间，设置结束时间
        if (StringUtils.equalsAnyIgnoreCase(testPlan.getStatus(), TestPlanStatus.Finished.name(), TestPlanStatus.Completed.name())
                && res.getActualEndTime() == null) {
            testPlan.setActualEndTime(System.currentTimeMillis());
        }

        // 如果当前状态不是已完成，设置结束时间为null
        if (!StringUtils.equalsAnyIgnoreCase(testPlan.getStatus(), TestPlanStatus.Finished.name(), TestPlanStatus.Completed.name())
                && res.getActualEndTime() != null) {
            testPlan.setActualEndTime(null);
        }

        // 如果当前状态不是未开始，并且没有开始时间，设置开始时间
        if (!StringUtils.equals(testPlan.getStatus(), TestPlanStatus.Prepare.name())
                && res.getActualStartTime() == null) {
            testPlan.setActualStartTime(System.currentTimeMillis());
        }
        mapper.update(testPlan);
        return mapper.selectOneById(testPlan.getId());
    }

    @Override
    public void checkTestPlanStatus(String planId) {
        if (countExecutingReportCount(planId) > 0) {
            return;
        }
        List<String> statusList = getExecResultByPlanId(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTestPlan(String id) {
        if (hasRunningReport(id)) {
            BusinessException.throwException("测试计划正在执行中");
        }
        testPlanPrincipalMapper.deleteByQuery(QueryChain.of(TestPlanPrincipal.class).where(TEST_PLAN_PRINCIPAL.TEST_PLAN_ID.eq(id)));
        testPlanFollowMapper.deleteByQuery(QueryChain.of(TestPlanFollow.class).where(TEST_PLAN_FOLLOW.TEST_PLAN_ID.eq(id)));
        testPlanTestCaseMapper.deleteByQuery(QueryChain.of(TestPlanTestCase.class).where(TEST_PLAN_TEST_CASE.PLAN_ID.eq(id)));
        testPlanReportMapper.deleteByQuery(QueryChain.of(TestPlanReport.class).where(TEST_PLAN_REPORT.TEST_PLAN_ID.eq(id)));
        return mapper.deleteById(id);
    }

    private boolean hasRunningReport(String id) {
        return QueryChain.of(TestPlanReport.class).where(TEST_PLAN_REPORT.TEST_PLAN_ID.eq(id)
                .and(TEST_PLAN_REPORT.STATUS.eq("RUNNING"))).exists();
    }

    private List<String> getExecResultByPlanId(String planId) {
        return null;
    }

    private long countExecutingReportCount(String planId) {
        return 0;
    }

    private void checkTestPlanExist(TestPlan testPlan) {
        if (Objects.nonNull(testPlan.getName())) {
            boolean exists = queryChain().where(TEST_PLAN.NAME.eq(testPlan.getName())
                    .and(TEST_PLAN.PROJECT_ID.eq(testPlan.getProjectId()))
                    .and(TEST_PLAN.ID.ne(testPlan.getId()))).exists();
            if (exists) {
                BusinessException.throwException("测试计划名称已存在");
            }
        }
    }
}
