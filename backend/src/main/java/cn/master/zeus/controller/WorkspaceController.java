package cn.master.zeus.controller;

import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.WorkspaceResource;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.Workspace;
import cn.master.zeus.service.IWorkspaceService;

import java.io.Serializable;
import java.util.List;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    private final IWorkspaceService iWorkspaceService;

    /**
     * 添加。
     *
     * @param workspace
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("/special/save")
    public Workspace save(@RequestBody Workspace workspace) {
        return iWorkspaceService.addWorkspaceByAdmin(workspace);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iWorkspaceService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param workspace
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/special/update")
    public int update(@RequestBody Workspace workspace) {
        return iWorkspaceService.updateWorkspaceByAdmin(workspace);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Workspace> list() {
        return iWorkspaceService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public Workspace getInfo(@PathVariable Serializable id) {
        return iWorkspaceService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<Workspace> page(@RequestBody BaseRequest request) {
        return iWorkspaceService.getWorkspacePage(request);
    }

    @GetMapping("/list/user/workspace")
    public List<Workspace> listUserWorkspace() {
        return iWorkspaceService.getWorkspaceListByUserId(SessionUtils.getUserId());
    }

    @PostMapping("/member/update")
    public void updateOrgMember(@RequestBody WorkspaceMemberDTO memberDTO) {
        iWorkspaceService.updateWorkspaceMember(memberDTO);
    }

    @GetMapping("/list/resource/{groupId}/{type}")
    public WorkspaceResource listResource(@PathVariable String groupId, @PathVariable String type) {
        return iWorkspaceService.listResource(groupId, type);
    }
}
