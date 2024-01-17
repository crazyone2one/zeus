package cn.master.zeus.entity;

import com.mybatisflex.annotation.*;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(value = "test_plan_node")
public class TestPlanNode implements Serializable {

    /**
     * Test case review node ID
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

    @Column(ignore = true)
    private String label;

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

    private Double pos;

    private String createUser;

    @Column(ignore = true)
    @RelationManyToOne(selfField = "parentId", targetField = "id")
    private TestPlanNode parent;

    @Column(ignore = true)
    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<TestPlanNode> children;

    @Column(ignore = true)
    private Integer caseNum;

}
