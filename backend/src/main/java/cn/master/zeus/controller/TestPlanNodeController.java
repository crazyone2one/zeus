package cn.master.zeus.controller;

import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.plan.QueryTestPlanRequest;
import cn.master.zeus.service.BaseCheckPermissionService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.master.zeus.entity.TestPlanNode;
import cn.master.zeus.service.ITestPlanNodeService;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan/node")
public class TestPlanNodeController {

    private final ITestPlanNodeService iTestPlanNodeService;
    private final BaseCheckPermissionService checkPermissionService;

    /**
     * 添加。
     *
     * @param testPlanNode
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+EDIT')")
    public String save(@RequestBody TestPlanNode testPlanNode) {
        return iTestPlanNodeService.addNode(testPlanNode);
    }

    /**
     * 根据主键删除。
     *
     * @param nodeIds 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @PostMapping("remove")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+EDIT')")
    public int remove(@RequestBody List<String> nodeIds) {
        return iTestPlanNodeService.deleteNode(nodeIds);
    }

    /**
     * 根据主键更新。
     *
     * @param testPlanNode
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+EDIT')")
    public boolean update(@RequestBody TestPlanNode testPlanNode) {
        return iTestPlanNodeService.updateById(testPlanNode);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlanNode> list() {
        return iTestPlanNodeService.list();
    }

    @PostMapping("/list/{projectId}")
    public List<TestPlanNode> listByProjectId(@PathVariable String projectId, @RequestBody(required = false) QueryTestPlanRequest request) {
        if (request != null && request.getProjectId() != null) {
            projectId = request.getProjectId();
        }
        checkPermissionService.checkProjectOwner(projectId);
        return iTestPlanNodeService.getNodeTree(projectId, Optional.ofNullable(request).orElse(new QueryTestPlanRequest()));
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlanNode getInfo(@PathVariable Serializable id) {
        return iTestPlanNodeService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<TestPlanNode> page(@RequestBody BaseRequest page) {
        return iTestPlanNodeService.getPageData(page);
    }

}
