package cn.master.zeus.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
    /**
     * 版本来源字段
     */
    private String refId;

    /**
     * 过滤条件
     */
    private Map<String, List<String>> filters;
    /**
     * 状态不等于 notEqStatus
     */
    private String notEqStatus;
    /**
     * 高级搜索
     */
    private Map<String, Object> combine;
}
