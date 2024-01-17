package cn.master.zeus.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "test_plan_report")
public class TestPlanReport implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * Test plan ID
     */
    private String testPlanId;

    /**
     * name
     */
    private String name;

    /**
     * report status
     */
    private String status;

    /**
     * test plan execute triggerMode
     */
    private String triggerMode;

    /**
     * report creator
     */
    private String creator;

    /**
     * report startTime
     */
    private Long startTime;

    /**
     * report timestamp
     */
    private Long endTime;

    /**
     * is Api Case executing
     */
    private Boolean isApiCaseExecuting;

    /**
     * is scenario Case executing
     */
    private Boolean isScenarioExecuting;

    /**
     * is Ui Scenario executing
     */
    private Boolean isUiScenarioExecuting;

    /**
     * is performance executing
     */
    private Boolean isPerformanceExecuting;

    /**
     * principal
     */
    private String principal;

    /**
     * Create timestamp
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * （0正常 1删除）
     */
    private Boolean delFlag;

    private String createUser;

    private String components;

    /**
     * v1.12报告改版标记
     */
    private Boolean isNew;

    /**
     * request (JSON format)
     */
    private String runInfo;

}
