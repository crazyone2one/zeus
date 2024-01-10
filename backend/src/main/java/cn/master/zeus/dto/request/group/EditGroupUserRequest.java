package cn.master.zeus.dto.request.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Created by 11's papa on 01/09/2024
 **/
@Setter
@Getter
public class EditGroupUserRequest {
    private List<String> userIds;
    private List<String> sourceIds;
    private String groupId;
}
