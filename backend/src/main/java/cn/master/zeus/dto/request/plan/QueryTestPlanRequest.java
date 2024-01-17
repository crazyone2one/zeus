package cn.master.zeus.dto.request.plan;

import cn.master.zeus.entity.TestPlan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 01/13/2024
 **/
@Setter
@Getter
public class QueryTestPlanRequest extends TestPlan {
    private boolean recent = false;

    private List<String> planIds;

    private String scenarioId;

    private String apiId;

    private String loadId;
    private Map<String, List<String>> filters;

    private Map<String, Object> combine;

    private String projectName;

    /**
     * 执行人或者负责人
     */
    private String executorOrPrincipal;

    /**
     * 是否通过筛选条件查询（这个字段针对我的工作台-页面列表上的筛选做特殊处理）
     */
    private boolean byFilter;

    private List<String> filterStatus;

    /**
     * @since 2.10.10 添加模块树条件, 批量移动条件
     */
    private List<String> nodeIds;
    private Boolean selectAll;
    private List<String> unSelectIds;
}
