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
@Table(value = "schedule")
public class Schedule implements Serializable {

    /**
     * Schedule ID
     */
    @Id
    private String id;

    /**
     * Schedule Key
     */
    private String key;

    /**
     * Schedule Type
     */
    private String type;

    /**
     * Schedule value
     */
    private String value;

    /**
     * Group Name
     */
    private String group;

    /**
     * Schedule Job Class Name
     */
    private String job;

    /**
     * Schedule Eable
     */
    private Boolean enable;

    /**
     * Resource Id
     */
    private String resourceId;

    /**
     * Change User
     */
    private String userId;

    /**
     * Custom Data (JSON format)
     */
    private String customData;

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

}
