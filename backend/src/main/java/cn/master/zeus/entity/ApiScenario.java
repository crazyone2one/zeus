package cn.master.zeus.entity;

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
@Table(value = "api_scenario")
public class ApiScenario implements Serializable {

    @Id
    private String id;

    /**
     * Project ID this test belongs to
     */
    private String projectId;

    /**
     * tag list
     */
    private String tags;

    /**
     * User ID
     */
    private String userId;

    /**
     * User ID
     */
    private String apiScenarioModuleId;

    private String modulePath;

    /**
     * api scenario name
     */
    private String name;

    /**
     * api scenario level 
     */
    private String level;

    /**
     * api scenario status 
     */
    private String status;

    /**
     * api scenario principal 
     */
    private String principal;

    /**
     * Step total 
     */
    private Integer stepTotal;

    /**
     * Test schedule (cron list)
     */
    private String schedule;

    /**
     * Test scenario_definition json
     */
    private String scenarioDefinition;

    /**
     * api scenario description
     */
    private String description;

    /**
     * Create timestamp
     */
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    private LocalDateTime updateTime;

    /**
     * （0正常 1删除）
     */
    private Boolean delFlag;

    private String createUser;

    private String passRate;

    private String lastResult;

    private String reportId;

    /**
     * api scenario ID
     */
    private Integer num;

    private String originalState;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * Delete timestamp
     */
    private Long deleteTime;

    /**
     * Delete user id
     */
    private String deleteUserId;

    /**
     * 自定义排序，间隔5000
     */
    private Long order;

    private String environmentType;

    private String environmentJson;

    private String environmentGroupId;

    /**
     * 是否为最新版本 0:否，1:是
     */
    private Boolean latest;

}
