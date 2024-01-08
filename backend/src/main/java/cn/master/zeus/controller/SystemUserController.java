package cn.master.zeus.controller;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.AddMemberRequest;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.user.SystemUserDTO;
import cn.master.zeus.dto.request.user.UserRequest;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.service.ISystemUserService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SystemUserController {

    private final ISystemUserService iSystemUserService;

    /**
     * 添加用户信息表。
     *
     * @param systemUser 用户信息表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("/special/save")
    public int save(@RequestBody cn.master.zeus.dto.request.member.UserRequest systemUser) {
        return iSystemUserService.insert(systemUser);
    }

    /**
     * 根据主键删除用户信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iSystemUserService.removeById(id);
    }

    /**
     * 根据主键更新用户信息表。
     *
     * @param systemUser 用户信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemUser systemUser) {
        return iSystemUserService.updateById(systemUser);
    }

    /**
     * 查询所有用户信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemUser> list() {
        return iSystemUserService.getUserList();
    }

    /**
     * 根据用户信息表主键获取详细信息。
     *
     * @param id 用户信息表主键
     * @return 用户信息表详情
     */
    @GetMapping("getInfo/{id}")
    public SystemUser getInfo(@PathVariable Serializable id) {
        return iSystemUserService.getById(id);
    }

    /**
     * 分页查询用户信息表。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("/special/page")
    @PreAuthorize("hasAuthority('SYSTEM_USER:READ')")
    public Page<SystemUserDTO> page(@RequestBody UserRequest request) {
        return iSystemUserService.getUserPageList(request);
    }

    @GetMapping("getCurrentUser")
    public Object getCurrentUser(Authentication authentication) {
        return authentication.getPrincipal();
    }

    @PostMapping("/special/ws/member/list")
    public Page<SystemUserDTO> getMemberListByAdmin(@RequestBody BaseRequest request) {
        return iSystemUserService.getMemberPage(request);
    }

    @PostMapping("/special/ws/member/add")
    public void addMemberByAdmin(@RequestBody AddMemberRequest request) {
        iSystemUserService.addMember(request);
    }

    @PostMapping("/special/update")
    public void update(@RequestBody cn.master.zeus.dto.request.member.UserRequest user) {
        iSystemUserService.updateUserRole(user);
    }

    @GetMapping("/switch/source/ws/{sourceId}")
    public UserDTO switchWorkspace(@PathVariable(value = "sourceId") String sourceId) {
        return iSystemUserService.switchUserResource("workspace", sourceId);
    }
}
