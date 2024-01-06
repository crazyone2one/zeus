package cn.master.zeus.dto;

import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.Workspace;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 01/05/2024
 **/
@Data
public class WorkspaceResource {
    private List<Workspace> workspaces = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
}
