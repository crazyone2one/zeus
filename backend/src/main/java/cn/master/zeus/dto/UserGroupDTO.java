package cn.master.zeus.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 01/08/2024
 **/
@Setter
@Getter
public class UserGroupDTO {
    private String userId;
    private String groupId;
    private String sourceId;
    private String name;
    /**
     * 用户组所属类型
     */
    private String type;
}
