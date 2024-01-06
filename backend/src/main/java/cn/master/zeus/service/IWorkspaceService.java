package cn.master.zeus.service;

import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.WorkspaceResource;
import cn.master.zeus.dto.request.BaseRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.Workspace;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IWorkspaceService extends IService<Workspace> {

    Page<Workspace> getWorkspacePage(BaseRequest request);

    List<Workspace> getWorkspaceListByUserId(String userId);

    Workspace addWorkspaceByAdmin(Workspace workspace);

    int updateWorkspaceByAdmin(Workspace workspace);

    void updateWorkspaceMember(WorkspaceMemberDTO memberDTO);

    WorkspaceResource listResource(String groupId, String type);
}
