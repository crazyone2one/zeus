package cn.master.zeus.dto.request;

import lombok.Data;

/**
 * @author Created by 11's papa on 01/04/2024
 **/
@Data
public class GroupRequest {
    private String resourceId;
    private String projectId;
    private String type;
}
