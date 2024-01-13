package cn.master.zeus.controller;

import cn.master.zeus.dto.TestCaseTemplateDao;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.UpdateCaseFieldTemplateRequest;
import cn.master.zeus.entity.TestCaseTemplate;
import cn.master.zeus.service.ITestCaseTemplateService;
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
@RequestMapping("/field/template/case")
@RequiredArgsConstructor
public class TestCaseTemplateController {

    private final ITestCaseTemplateService iTestCaseTemplateService;

    /**
     * 添加。
     *
     * @param request
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CASE_TEMPLATE')")
    public void save(@RequestBody UpdateCaseFieldTemplateRequest request) {
        iTestCaseTemplateService.add(request);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CASE_TEMPLATE')")
    public void remove(@PathVariable String id) {
        iTestCaseTemplateService.delete(id);
    }

    /**
     * 根据主键更新。
     *
     * @param request
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CASE_TEMPLATE')")
    public void update(@RequestBody UpdateCaseFieldTemplateRequest request) {
        iTestCaseTemplateService.updateTestCaseTemplate(request);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestCaseTemplate> list() {
        return iTestCaseTemplateService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestCaseTemplate getInfo(@PathVariable Serializable id) {
        return iTestCaseTemplateService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CASE_TEMPLATE')")
    public Page<TestCaseTemplate> page(@RequestBody BaseRequest request) {
        return iTestCaseTemplateService.getPage(request);
    }

    @GetMapping({"/option/{projectId}", "/option"})
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CASE_TEMPLATE')")
    public List<TestCaseTemplate> option(@PathVariable(required = false) String projectId) {
        return iTestCaseTemplateService.getOption(projectId);
    }

    @GetMapping("/get/relate/{projectId}")
    public TestCaseTemplateDao getTemplate(@PathVariable String projectId) {
        return iTestCaseTemplateService.getTemplate(projectId);
    }

    @GetMapping("/get/relate/simple/{projectId}")
    public TestCaseTemplateDao getTemplateSimple(@PathVariable String projectId) {
        return iTestCaseTemplateService.getTemplateForList(projectId);
    }
}
