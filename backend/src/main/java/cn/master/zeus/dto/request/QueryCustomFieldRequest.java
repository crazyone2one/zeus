package cn.master.zeus.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 01/11/2024
 **/
@Setter
@Getter
public class QueryCustomFieldRequest extends BaseRequest {
    private String templateId;
    private String scene;
    private List<String> templateContainIds;
    private List<String> ids;
    /**
     * 过滤条件
     */
    private Map<String, List<String>> filters;
}
