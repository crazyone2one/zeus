package cn.master.zeus.service;

import cn.master.zeus.dto.UserDTO;
import cn.master.zeus.dto.request.AddMemberRequest;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.dto.request.user.SystemUserDTO;
import cn.master.zeus.dto.request.user.UserRequest;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户信息表 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ISystemUserService extends IService<SystemUser> {

    List<SystemUser> getMemberList(QueryMemberRequest queryMemberRequest);

    Page<SystemUserDTO> getMemberPage(BaseRequest request);

    List<SystemUser> getUserList();

    void addMember(AddMemberRequest request);

    Page<SystemUserDTO> getUserPageList(UserRequest request);

    void updateUserRole(cn.master.zeus.dto.request.member.UserRequest user);

    int insert(cn.master.zeus.dto.request.member.UserRequest systemUser);

    UserDTO switchUserResource(String sign, String sourceId);

    UserDTO getUserDTO(String userId);

    /**
     * 查询项目所属用户
     *
     * @param request 查询条件
     * @return com.mybatisflex.core.paginate.Page<cn.master.zeus.entity.SystemUser>
     */
    Page<SystemUserDTO> getProjectMemberPage(BaseRequest request);

    UserDTO updateCurrentUser(SystemUser user);
    void updateUser(SystemUser user);
}
