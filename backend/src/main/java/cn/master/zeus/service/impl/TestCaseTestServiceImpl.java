package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.TestCaseTest;
import cn.master.zeus.mapper.TestCaseTestMapper;
import cn.master.zeus.service.ITestCaseTestService;
import org.springframework.stereotype.Service;

/**
 * 测试用例和关联用例的关系表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class TestCaseTestServiceImpl extends ServiceImpl<TestCaseTestMapper, TestCaseTest> implements ITestCaseTestService {

}
