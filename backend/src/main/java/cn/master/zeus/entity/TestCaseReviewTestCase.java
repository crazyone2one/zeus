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
@Table(value = "test_case_review_test_case")
public class TestCaseReviewTestCase implements Serializable {

    @Id
    private String id;

    private String reviewId;

    private String caseId;

    private String status;

    private String result;

    private String reviewer;

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
     * 自定义排序，间隔5000
     */
    private Long order;

    /**
     * 关联的用例是否放入回收站
     */
    private Boolean isDel;

}
