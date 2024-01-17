package cn.master.zeus.service;

import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestPlanTestCase;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestPlanTestCaseService extends IService<TestPlanTestCase> {

    int deleteToGc(List<String> caseIds);
}
