package cn.master.zeus.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Created by 11's papa on 01/04/2024
 **/
@Setter
@Getter
public class AddMemberRequest {
    private String workspaceId;
    private List<String> userIds;
    private List<String> roleIds;
    private List<String> groupIds;
    private String projectId;
}
