package cn.master.zeus.service;

import cn.master.zeus.dto.TestCaseDTO;
import cn.master.zeus.dto.request.testcase.EditTestCaseRequest;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestCase;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestCaseService extends IService<TestCase> {

    TestCase add(EditTestCaseRequest request);

    TestCase addTestCase(EditTestCaseRequest request);

    int getNextNum(String projectId);

    void saveFollows(String caseId, List<String> follows);

    List<String> getFollows(String caseId);

    List<TestCaseDTO> getTestCaseVersions(String caseId);

    List<TestCaseDTO> listTestCase(QueryTestCaseRequest request);

    List<TestCaseDTO> listTestCase(QueryTestCaseRequest request,boolean isSampleInfo);

    Page<TestCaseDTO> pageTestCase(QueryTestCaseRequest request);
}
