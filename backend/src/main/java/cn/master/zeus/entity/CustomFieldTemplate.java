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
@Table(value = "custom_field_template")
public class CustomFieldTemplate implements Serializable {

    /**
     * Custom field field template related id
     */
    @Id
    private String id;

    /**
     * Custom field ID
     */
    private String fieldId;

    /**
     * Field template ID
     */
    private String templateId;

    /**
     * Use scene
     */
    private String scene;

    /**
     * Is required
     */
    private Boolean required;

    /**
     * Item order
     */
    private Integer order;

    private String defaultValue;

    /**
     * Custom data
     */
    private String customData;

    /**
     * Save Table Header Key
     */
    private String key;

    /**
     * Create timestamp
     */
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    private LocalDateTime updateTime;

    private String createUser;

    private String updateUser;

    private Boolean delFlag;

}
