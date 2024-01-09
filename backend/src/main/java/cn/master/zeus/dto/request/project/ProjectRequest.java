package cn.master.zeus.dto.request.project;

import cn.master.zeus.dto.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 01/08/2024
 **/
@Setter
@Getter
public class ProjectRequest extends BaseRequest {
    private String userId;
    private Map<String, List<String>> filters;
    private Map<String, Object> combine;
}
