package cn.master.zeus.controller;

import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.testcase.DragNodeRequest;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import cn.master.zeus.entity.TestCaseNode;
import cn.master.zeus.service.BaseCheckPermissionService;
import cn.master.zeus.service.ITestCaseNodeService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/case/node")
public class TestCaseNodeController {

    private final ITestCaseNodeService iTestCaseNodeService;
    private final BaseCheckPermissionService baseCheckPermissionService;

    /**
     * 添加。
     *
     * @param testCaseNode
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+EDIT')")
    public String save(@RequestBody TestCaseNode testCaseNode) {
        return iTestCaseNodeService.addNode(testCaseNode);
    }

    /**
     * 根据主键删除。
     *
     * @param nodeIds 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+EDIT')")
    public int remove(@RequestBody List<String> nodeIds) {
        return iTestCaseNodeService.deleteNode(nodeIds);
    }

    /**
     * 根据主键更新。
     *
     * @param node
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+EDIT')")
    public boolean update(@RequestBody DragNodeRequest node) {
        return iTestCaseNodeService.updateById(node);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public List<TestCaseNode> getNodeByProjectId(@PathVariable String projectId) {
        baseCheckPermissionService.checkProjectOwner(projectId);
        return iTestCaseNodeService.getNodeTreeByProjectId(projectId);
    }

    @PostMapping("list/{projectId}")
    //@PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public List<TestCaseNode> getNodeByCondition(@PathVariable String projectId, @RequestBody(required = false) QueryTestCaseRequest request) {
        if (request != null && request.getProjectId() != null) {
            projectId = request.getProjectId();
        }
        baseCheckPermissionService.checkProjectOwner(projectId);
        return iTestCaseNodeService.getNodeTreeByProjectId(projectId, Optional.ofNullable(request).orElse(new QueryTestCaseRequest()));
    }

    @PostMapping("/relationship/list/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public List<TestCaseNode> getRelationshipNodeByCondition(@PathVariable String projectId, @RequestBody(required = false) QueryTestCaseRequest request) {
        // 高级搜索所属模块搜索时, 切换项目时需替换projectId为参数中切换项目
        if (request != null && request.getProjectId() != null) {
            projectId = request.getProjectId();
        }
        baseCheckPermissionService.checkProjectOwner(projectId);
        return iTestCaseNodeService.getRelationshipNodeByCondition(projectId, Optional.ofNullable(request).orElse(new QueryTestCaseRequest()));
    }

    @PostMapping("/count/{projectId}")
    public Map<String, Integer> getNodeCountMapByProjectId(@PathVariable String projectId, @RequestBody(required = false) QueryTestCaseRequest request) {
        baseCheckPermissionService.checkProjectOwner(projectId);
        return iTestCaseNodeService.getNodeCountMapByProjectId(projectId, Optional.ofNullable(request).orElse(new QueryTestCaseRequest()));
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestCaseNode getInfo(@PathVariable Serializable id) {
        return iTestCaseNodeService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public Page<TestCaseNode> page(@RequestBody QueryTestCaseRequest request) {
        return iTestCaseNodeService.pageNode(request);
    }

}
