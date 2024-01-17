package cn.master.zeus.controller;

import cn.master.zeus.common.enums.ProjectApplicationType;
import cn.master.zeus.dto.TestCaseDTO;
import cn.master.zeus.dto.request.testcase.EditTestCaseRequest;
import cn.master.zeus.dto.request.testcase.QueryTestCaseRequest;
import cn.master.zeus.entity.ProjectApplication;
import cn.master.zeus.service.IProjectApplicationService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.TestCase;
import cn.master.zeus.service.ITestCaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/case")
public class TestCaseController {

    private final ITestCaseService iTestCaseService;
    private final IProjectApplicationService projectApplicationService;

    /**
     * 添加。
     *
     * @param request
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping(value = "add", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+CREATE')")
    public TestCase save(@RequestPart("request") EditTestCaseRequest request, @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return iTestCaseService.add(request);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+CREATE')")
    public TestCase saveTestCase(@RequestBody EditTestCaseRequest request) {
        //request.setId(UUID.randomUUID().toString());
        return iTestCaseService.addTestCase(request);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTestCaseService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param testCase
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TestCase testCase) {
        return iTestCaseService.updateById(testCase);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestCase> list() {
        return iTestCaseService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestCase getInfo(@PathVariable Serializable id) {
        return iTestCaseService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public Page<TestCaseDTO> page(@RequestBody QueryTestCaseRequest request) {
        ProjectApplication projectApplication = projectApplicationService.getProjectApplication(request.getProjectId(), ProjectApplicationType.CASE_CUSTOM_NUM.name());
        if (projectApplication != null && StringUtils.isNotEmpty(projectApplication.getTypeValue()) && request.getCombine() != null) {
            request.getCombine().put("caseCustomNum", projectApplication.getTypeValue());
        }
        return iTestCaseService.pageTestCase(request);
    }

    @GetMapping("/follow/{caseId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public List<String> getFollows(@PathVariable String caseId) {
        return iTestCaseService.getFollows(caseId);
    }

    @PostMapping("/edit/follows/{caseId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ+EDIT')")
    public void editTestFollows(@PathVariable String caseId, @RequestBody List<String> follows) {
        iTestCaseService.saveFollows(caseId, follows);
    }

    @GetMapping("versions/{caseId}")
    @PreAuthorize("hasAuthority('PROJECT_TRACK_CASE:READ')")
    public List<TestCaseDTO>  versionList(@PathVariable String caseId) {
        return iTestCaseService.getTestCaseVersions(caseId);
    }
}
