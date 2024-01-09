package cn.master.zeus.controller;

import cn.master.zeus.dto.GroupDTO;
import cn.master.zeus.dto.GroupPermissionDTO;
import cn.master.zeus.dto.request.GroupRequest;
import cn.master.zeus.dto.request.group.EditGroupRequest;
import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.service.ISystemGroupService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user/group")
@RequiredArgsConstructor
public class SystemGroupController {

    private final ISystemGroupService iSystemGroupService;

    /**
     * 添加。
     *
     * @param request
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public SystemGroup save(@RequestBody EditGroupRequest request) {
        return iSystemGroupService.addGroup(request);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        iSystemGroupService.deleteGroup(id);
    }

    /**
     * 根据主键更新。
     *
     * @param request
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public void update(@RequestBody EditGroupRequest request) {
        iSystemGroupService.editGroup(request);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @PostMapping("list")
    public List<SystemGroup> list(@RequestBody GroupRequest request) {
        return iSystemGroupService.getGroupsByType(request);
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public SystemGroup getInfo(@PathVariable Serializable id) {
        return iSystemGroupService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<GroupDTO> page(@RequestBody EditGroupRequest request) {
        return iSystemGroupService.getGroupPage(request);
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getAllUserGroup(@PathVariable("userId") String userId) {
        return iSystemGroupService.getAllUserGroup(userId);
    }

    @PostMapping("/get")
    public List<SystemGroup> getGroupByType(@RequestBody EditGroupRequest request) {
        return iSystemGroupService.getGroupByType(request);
    }

    @PostMapping("/permission")
    public GroupPermissionDTO getGroupResource(@RequestBody SystemGroup group) {
        return iSystemGroupService.getGroupResource(group);
    }
    @PostMapping("/permission/edit")
    public void editGroupPermission(@RequestBody EditGroupRequest editGroupRequest) {
        iSystemGroupService.editGroupPermission(editGroupRequest);
    }
}
