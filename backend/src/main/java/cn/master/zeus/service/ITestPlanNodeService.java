package cn.master.zeus.service;

import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.plan.QueryTestPlanRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestPlanNode;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestPlanNodeService extends IService<TestPlanNode> {

    String addNode(TestPlanNode testPlanNode);

    int deleteNode(List<String> nodeIds);

    List<TestPlanNode> getNodeTree(String projectId, QueryTestPlanRequest queryTestPlanRequest);

    void createDefaultNode(String projectId);

    Page<TestPlanNode> getPageData(BaseRequest page);
}
