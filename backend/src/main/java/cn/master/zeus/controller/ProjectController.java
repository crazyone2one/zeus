package cn.master.zeus.controller;

import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.request.project.ProjectRequest;
import cn.master.zeus.entity.Project;
import cn.master.zeus.service.IProjectService;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService iProjectService;

    /**
     * 添加。
     *
     * @param project
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public Project save(@RequestBody Project project) {
        return iProjectService.addProject(project);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iProjectService.deleteProject(id);
    }

    /**
     * 根据主键更新。
     *
     * @param project
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public void update(@RequestBody Project project) {
        iProjectService.updateProject(project);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Project> list() {
        return iProjectService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public Project getInfo(@PathVariable Serializable id) {
        return iProjectService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<Project> page(@RequestBody ProjectRequest request) {
        if (StringUtils.isBlank(request.getWorkspaceId())) {
            return new Page<>();
        }
        return iProjectService.getProjectPage(request);
    }

    @GetMapping("/member/size/{id}")
    public long getProjectMemberSize(@PathVariable String id) {
        return iProjectService.getProjectMemberSize(id);
    }

    @PostMapping("/member/update")
    public void updateMember(@RequestBody WorkspaceMemberDTO memberDTO) {
        iProjectService.updateMember(memberDTO);
    }

    @GetMapping("/list/all/{workspaceId}")
    public List<Project> listAll(@PathVariable String workspaceId) {
        //String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        ProjectRequest request = new ProjectRequest();
        request.setWorkspaceId(workspaceId);
        return iProjectService.getProjectList(request);
    }
}
