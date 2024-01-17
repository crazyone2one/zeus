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
@Table(value = "test_plan_ui_scenario")
public class TestPlanUiScenario implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * Test plan ID
     */
    private String testPlanId;

    private String uiScenarioId;

    /**
     * Scenario case status
     */
    private String status;

    /**
     * Relevance environment
     */
    private String environment;

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

    private String passRate;

    private String lastResult;

    private String reportId;

    private String createUser;

    /**
     * 排序，默认值5000
     */
    private Long order;

    private String environmentType;

    /**
     * ID
     */
    private String environmentGroupId;

}
