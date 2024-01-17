package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.TestPlanTestCase;
import cn.master.zeus.mapper.TestPlanTestCaseMapper;
import cn.master.zeus.service.ITestPlanTestCaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class TestPlanTestCaseServiceImpl extends ServiceImpl<TestPlanTestCaseMapper, TestPlanTestCase> implements ITestPlanTestCaseService {

    @Override
    public int deleteToGc(List<String> caseIds) {
        return updateIsDel(caseIds, true);
    }

    private int updateIsDel(List<String> caseIds, boolean isDel) {
        return 0;
    }
}
