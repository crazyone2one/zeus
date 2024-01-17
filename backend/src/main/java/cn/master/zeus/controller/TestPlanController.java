package cn.master.zeus.controller;

import cn.master.zeus.common.constants.StatusReference;
import cn.master.zeus.dto.request.plan.AddTestPlanRequest;
import cn.master.zeus.entity.TestPlan;
import cn.master.zeus.service.BaseCheckPermissionService;
import cn.master.zeus.service.ITestPlanService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/plan")
public class TestPlanController {

    private final ITestPlanService iTestPlanService;
    private final BaseCheckPermissionService checkPermissionService;

    /**
     * 添加。
     *
     * @param testPlan
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+CREATE')")
    public TestPlan save(@RequestBody AddTestPlanRequest testPlan) {
        TestPlan result = iTestPlanService.addTestPlan(testPlan);
        result.setStage(StatusReference.statusMap.containsKey(result.getStage()) ? StatusReference.statusMap.get(result.getStage()) : result.getStage());
        result.setStatus(StatusReference.statusMap.containsKey(result.getStatus()) ? StatusReference.statusMap.get(result.getStatus()) : result.getStatus());
        return result;
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @PostMapping("remove/{id}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+DELETE')")
    public int remove(@PathVariable String id) {
        checkPermissionService.checkTestPlanOwner(id);
        return iTestPlanService.deleteTestPlan(id);
    }

    /**
     * 根据主键更新。
     *
     * @param testPlanDTO
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ+EDIT')")
    public TestPlan update(@RequestBody AddTestPlanRequest testPlanDTO) {
        TestPlan testPlan = iTestPlanService.editTestPlanWithRequest(testPlanDTO);
        testPlan.setStage(StatusReference.statusMap.containsKey(testPlan.getStage()) ? StatusReference.statusMap.get(testPlan.getStage()) : testPlan.getStage());
        testPlan.setStatus(StatusReference.statusMap.containsKey(testPlan.getStatus()) ? StatusReference.statusMap.get(testPlan.getStatus()) : testPlan.getStatus());
        return testPlan;
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlan> list() {
        return iTestPlanService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlan getInfo(@PathVariable Serializable id) {
        return iTestPlanService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlan> page(Page<TestPlan> page) {
        return iTestPlanService.page(page);
    }

    @PostMapping("/fresh/{planId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ')")
    public TestPlan fresh(@PathVariable("planId") String planId) {
        AddTestPlanRequest request = new AddTestPlanRequest();
        request.setId(planId);
        return iTestPlanService.editTestPlanWithRequest(request);
    }

    @PostMapping("/edit/status/{planId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_PLAN:READ')")
    public void editTestPlanStatus(@PathVariable("planId") String planId) {
        checkPermissionService.checkTestPlanOwner(planId);
        iTestPlanService.checkTestPlanStatus(planId);
    }
}
