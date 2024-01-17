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
@Table(value = "test_plan_test_case")
public class TestPlanTestCase implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * Plan ID relation to
     */
    private String planId;

    /**
     * Case ID relation to
     */
    private String caseId;

    /**
     * Test report ID relation to
     */
    private String reportId;

    /**
     * Test case executor
     */
    private String executor;

    /**
     * Test case status
     */
    private String status;

    /**
     * Test case result
     */
    private String results;

    /**
     * Test case result issues
     */
    private String issues;

    /**
     * Test case remark
     */
    private String remark;

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

    private String actualResult;

    /**
     * 关联的用例是否放入回收站
     */
    private Boolean isDel;

}
