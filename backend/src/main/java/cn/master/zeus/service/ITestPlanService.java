package cn.master.zeus.service;

import cn.master.zeus.dto.request.plan.AddTestPlanRequest;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestPlan;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestPlanService extends IService<TestPlan> {

    TestPlan addTestPlan(AddTestPlanRequest testPlan);

    TestPlan editTestPlanWithRequest(AddTestPlanRequest testPlanDTO);

    TestPlan editTestPlan(TestPlan testPlan);

    void checkTestPlanStatus(String planId);

    int deleteTestPlan(String id);
}
