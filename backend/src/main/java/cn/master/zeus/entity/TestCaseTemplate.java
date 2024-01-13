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
@Table(value = "test_case_template")
public class TestCaseTemplate implements Serializable {

    /**
     * Field template ID
     */
    @Id
    private String id;

    /**
     * Field template name
     */
    private String name;

    /**
     * Field template type
     */
    private String type;

    /**
     * Field template description
     */
    private String description;

    /**
     * Test Case Name
     */
    private String caseName;

    /**
     * Is system field template 
     */
    private Boolean system;

    /**
     * Is global template
     */
    private Boolean global;

    /**
     * Workspace ID this field template belongs to
     */
    private String workspaceId;

    /**
     * Test case prerequisite
     */
    private String prerequisite;

    /**
     * Test case steps desc
     */
    private String stepDescription;

    /**
     * Test case expected result
     */
    private String expectedResult;

    /**
     * Test case actual result
     */
    private String actualResult;

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
     * Step model
     */
    private String stepModel;

    /**
     * Test case step
     */
    private String steps;

    private String projectId;

}
