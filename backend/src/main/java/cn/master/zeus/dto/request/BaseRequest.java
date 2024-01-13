package cn.master.zeus.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Data
public class BaseRequest {
    private long pageNumber;
    private long pageSize;
    private String name;
    private String workspaceId;
    private String projectId;
    private List<String> ids;

    private List<String> moduleIds;

    private List<String> nodeIds;

    /**
     * 排除哪些id
     */
    private List<String> notInIds;
}
