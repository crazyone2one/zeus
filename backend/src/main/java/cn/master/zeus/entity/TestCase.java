package cn.master.zeus.entity;

import com.mybatisflex.annotation.*;

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
@Table(value = "test_case")
public class TestCase implements Serializable {

    /**
     * Test case ID
     */
    @Id
    private String id;

    /**
     * Node ID this case belongs to
     */
    private String nodeId;

    private String testId;

    /**
     * Node path this case belongs to
     */
    private String nodePath;

    /**
     * Project ID this test belongs to
     */
    private String projectId;
    @RelationOneToOne(selfField = "projectId",targetTable = "project",targetField = "id",valueField = "projectName")
    private String projectName;

    /**
     * Test case name
     */
    private String name;

    /**
     * Test case type
     */
    private String type;

    /**
     * Test case maintainer
     */
    private String maintainer;

    @RelationOneToOne(selfField = "maintainer",targetTable = "system_user",targetField = "id",valueField = "maintainerName")
    private String maintainerName;

    /**
     * Test case priority
     */
    private String priority;

    /**
     * Test case method type
     */
    private String method;

    /**
     * Test case prerequisite condition
     */
    private String prerequisite;

    /**
     * Test case remark
     */
    private String remark;

    /**
     * Test case steps (JSON format)
     */
    private String steps;

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

    //@Column(ignore = true)
    @RelationOneToOne(selfField = "createUser",targetTable = "system_user",targetField = "id",valueField = "createName")
    private String createName;

    /**
     * Manually controlled growth identifier
     */
    private Integer num;

    private String reviewStatus;

    private String tags;

    private String demandId;

    private String demandName;

    private String status;

    private String stepDescription;

    private String expectedResult;

    /**
     * CustomField
     */
    private String customFields;

    /**
     * Test case step model
     */
    private String stepModel;

    /**
     * custom num
     */
    private String customNum;

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
     * 自定义排序，间隔5000
     */
    private Long order;

    /**
     * 是否是公共用例
     */
    private Boolean casePublic;

    /**
     * 版本ID
     */
    private String versionId;

    /**
     * 指向初始版本ID
     */
    private String refId;

    /**
     * 是否为最新版本 0:否，1:是
     */
    private Boolean latest;

    /**
     * 最近一次的测试计划的执行结果
     */
    private String lastExecuteResult;

}
