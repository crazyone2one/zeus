package cn.master.zeus.dto.request.testcase;

import cn.master.zeus.dto.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Setter
@Getter
public class QueryTestCaseRequest extends BaseRequest {
    private String id;

    private String name;

    private String relationshipType;

    private List<String> testCaseIds;

    // 测试计划是否允许重复
    private boolean repeatCase;

    private String planId;

    private String issuesId;

    private String userId;

    private String reviewId;

    private boolean isSelectThisWeedData = false;
    private boolean isSelectThisWeedRelevanceData = false;

    private String caseCoverage;

    private String nodeId;

    private String statusIsNot;

    private LocalDateTime createTime;
    private LocalDateTime relevanceCreateTime;
    private List<String> testCaseContainIds;
    private String relationshipCaseId;


    // 接口定义
    private String protocol;
    private String apiCaseCoverage;

    // 补充场景条件
    private String excludeId;
    private String moduleId;
    private boolean recent = false;
    private String executeStatus;
    private boolean notInTestPlan;
    //操作人
    private String operator;
    //操作时间
    private Long operationTime;
    private boolean casePublic;
    private long scheduleCreateTime;
    private String stepTotal;
    private Boolean toBeUpdated;
    private String apiCoverage;
    private String scenarioCoverage;

    /**
     * 是否查询UI
     */
    private boolean queryUi;

    /**
     * 是否待办状态
     */
    private boolean unComing;

    /**
     * 是否开启了用例自定义ID选项
     */
    private boolean customNum;
}
