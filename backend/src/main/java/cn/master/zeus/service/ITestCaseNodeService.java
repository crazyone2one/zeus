package cn.master.zeus.service;

import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.testcase.DragNodeRequest;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestCaseNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestCaseNodeService extends IService<TestCaseNode> {

    List<TestCaseNode> getNodeTreeByProjectId(String projectId);

    List<TestCaseNode> getNodeTreeByProjectId(String projectId, QueryTestCaseRequest queryTestCaseRequest);

    List<TestCaseNode> getRelationshipNodeByCondition(String projectId, QueryTestCaseRequest request);

    Map<String, Integer> getNodeCountMapByProjectId(String projectId, QueryTestCaseRequest request);

    TestCaseNode checkDefaultNode(String projectId);

    Page<TestCaseNode> pageNode(QueryTestCaseRequest request);

    String addNode(TestCaseNode testCaseNode);

    @Transactional(rollbackFor = Exception.class)
    int deleteNode(List<String> nodeIds);
}
