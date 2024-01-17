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
@Table(value = "test_plan")
public class TestPlan implements Serializable {

    /**
     * Test Plan ID
     */
    @Id
    private String id;

    /**
     * Workspace ID this plan belongs to
     */
    private String workspaceId;

    /**
     * Test plan report
     */
    private String reportId;

    /**
     * Plan name
     */
    private String name;

    /**
     * Plan description
     */
    private String description;

    /**
     * Plan status
     */
    private String status;

    /**
     * Plan stage
     */
    private String stage;

    /**
     * Test plan tags (JSON format)
     */
    private String tags;

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

    /**
     * Planned start time timestamp
     */
    private Long plannedStartTime;

    /**
     * Planned end time timestamp
     */
    private Long plannedEndTime;

    /**
     * Actual start time timestamp
     */
    private Long actualStartTime;

    /**
     * Actual end time timestamp
     */
    private Long actualEndTime;

    private String creator;

    /**
     * 测试计划所属项目
     */
    private String projectId;

    private Integer executionTimes;

    /**
     * 是否自定更新功能用例状态
     */
    private Boolean automaticStatusUpdate;

    /**
     * 测试计划报告配置
     */
    private String reportConfig;

    /**
     * 是否允许重复添加用例
     */
    private Boolean repeatCase;

    /**
     * request (JSON format)
     */
    private String runModeConfig;

    private String nodeId;
    private String nodePath;

}
