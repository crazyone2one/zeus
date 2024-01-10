package cn.master.zeus.service;

/**
 * @author Created by 11's papa on 01/10/2024
 **/
public interface BaseCheckPermissionService {
    /**
     * 严重project是否属于workspace
     *
     * @param projectId   project id
     * @param workspaceId workspace id
     */
    void checkProjectBelongToWorkspace(String projectId, String workspaceId);
}
