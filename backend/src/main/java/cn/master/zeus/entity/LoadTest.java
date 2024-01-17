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
@Table(value = "load_test")
public class LoadTest implements Serializable {

    /**
     * Test ID
     */
    @Id
    private String id;

    /**
     * Project ID this test belongs to
     */
    private String projectId;

    /**
     * Test name
     */
    private String name;

    /**
     * Test description
     */
    private String description;

    /**
     * Load configuration (JSON format)
     */
    private String loadConfiguration;

    /**
     * Load configuration (JSON format)
     */
    private String advancedConfiguration;

    /**
     * Test schedule (cron list)
     */
    private String schedule;

    /**
     * Create timestamp
     */
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    private LocalDateTime updateTime;

    /**
     * Test Status Running, Completed, Error, etc.
     */
    private String status;

    private String testResourcePoolId;

    private String userId;

    private Integer num;

    private String versionId;

    private String refId;

    /**
     * 是否为最新版本 0:否，1:是
     */
    private Boolean latest;

    private String createUser;

    /**
     * 关联的接口自动化版本号
     */
    private Integer scenarioVersion;

    /**
     * 关联的场景自动化ID
     */
    private String scenarioId;

    /**
     * 自定义排序，间隔5000
     */
    private Long order;

}
