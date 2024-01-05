package cn.master.zeus.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 01/05/2024
 **/
@Setter
@Getter
public class WorkspaceMemberDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String language;
    private String workspaceId;
    private String projectId;
    private List<String> roleIds = new ArrayList<>();
    private List<String> groupIds = new ArrayList<>();
}
