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
@Table(value = "api_test_case")
public class ApiTestCase implements Serializable {

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
     * Test case name
     */
    private String name;

    /**
     * priority
     */
    private String priority;

    /**
     * api definition id
     */
    private String apiDefinitionId;

    /**
     * Test description
     */
    private String description;

    /**
     * request (JSON format)
     */
    private String request;

    /**
     * User ID
     */
    private String createUserId;

    /**
     * User ID
     */
    private String updateUserId;

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

    /**
     * api test case ID
     */
    private Integer num;

    private String tags;

    /**
     * Last ApiDefinitionExecResult ID
     */
    private String lastResultId;

    private String status;

    private String originalStatus;

    /**
     * Delete timestamp
     */
    private Long deleteTime;

    /**
     * Delete user id
     */
    private String deleteUserId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 自定义排序，间隔5000
     */
    private Long order;

    /**
     * 用例状态等同场景的status
     */
    private String caseStatus;

    private String versionId;

    /**
     * 是否需要同步
     */
    private Boolean toBeUpdated;

}
