package cn.master.zeus.dto.request.group;

import cn.master.zeus.dto.GroupPermission;
import cn.master.zeus.entity.SystemGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 01/05/2024
 **/
@Setter
@Getter
public class EditGroupRequest extends SystemGroup {
    private long pageNumber;
    private long pageSize;
    private List<String> types = new ArrayList<>();
    private List<String> scopes = new ArrayList<>();

    /**
     * 是否是全局用户组
     */
    private Boolean global;

    private String projectId;
    private String userGroupId;
    private List<GroupPermission> permissions;
    private boolean onlyQueryCurrentProject = false;
    private boolean onlyQueryGlobal = false;
}
