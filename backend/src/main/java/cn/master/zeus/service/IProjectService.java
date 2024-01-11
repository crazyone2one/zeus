package cn.master.zeus.service;

import cn.master.zeus.dto.WorkspaceMemberDTO;
import cn.master.zeus.dto.request.AddMemberRequest;
import cn.master.zeus.dto.request.project.ProjectRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.Project;

import java.io.Serializable;
import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface IProjectService extends IService<Project> {

    Page<Project> getProjectPage(ProjectRequest request);

    Project addProject(Project project);

    long getProjectMemberSize(String id);

    void deleteProject(String id);

    void updateProject(Project project);

    void updateMember(WorkspaceMemberDTO memberDTO);

    List<Project> getProjectList(ProjectRequest request);

    List<Project> getUserProject(ProjectRequest request);
}
