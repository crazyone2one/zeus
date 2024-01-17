package cn.master.zeus.dto;

import lombok.Data;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Data
public class ProjectConfig {

    private String trackShareReportTime;
    private String performanceShareReportTime;
    private String apiShareReportTime;
    private String uiShareReportTime;
    private Boolean caseCustomNum = false;
    private Boolean scenarioCustomNum = false;
    private String apiQuickMenu;
    private String uiQuickMenu;
    private Boolean casePublic = false;
    private Integer mockTcpPort = 0;
    private Boolean mockTcpOpen = false;
    private Boolean cleanTrackReport = false;
    private String cleanTrackReportExpr;
    private Boolean cleanApiReport = false;
    private String cleanApiReportExpr;
    private Boolean cleanLoadReport = false;
    private String cleanLoadReportExpr;
    private Boolean cleanUiReport = false;
    private String cleanUiReportExpr;
    private Boolean urlRepeatable = false;
    private String triggerUpdate;
    private Boolean openUpdateTime = false;
    private String openUpdateRuleTime;
    private Boolean openUpdateRule;
    private String resourcePoolId;
    private Boolean poolEnable = false;
    private Boolean reReview = false;
    private String performanceScriptReviewer;
    private Boolean performanceReviewLoadTestScript = false;
    private String apiScriptReviewer;
    private Boolean apiReviewTestScript = false;
}
