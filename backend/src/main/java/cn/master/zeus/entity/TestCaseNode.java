package cn.master.zeus.entity;

import com.mybatisflex.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(value = "test_case_node")
public class TestCaseNode implements Serializable {

    /**
     * Test case node ID
     */
    @Id
    private String id;

    /**
     * Project ID this node belongs to
     */
    private String projectId;

    /**
     * Node name
     */
    private String name;

    /**
     * Parent node ID
     */
    private String parentId;

    /**
     * Node level
     */
    private Integer level;

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

    private Double pos;

    @Column(ignore = true)
    @RelationManyToOne(selfField = "parentId", targetField = "id")
    private TestCaseNode parent;

    @Column(ignore = true)
    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<TestCaseNode> children;

    @Column(ignore = true)
    private Integer caseNum;

    @Column(ignore = true)
    private String label;

}
