package cn.master.zeus.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "system_user")
public class SystemUser implements Serializable {

    /**
     * User ID
     */
    @Id
    private String id;

    /**
     * User name
     */
    private String name;

    /**
     * E-Mail address
     */
    private String email;

    /**
     * user name/nickname
     */
    private String userName;

    //@ColumnMask(Masks.PASSWORD)
    private String password;

    /**
     * User status
     */
    private Boolean status;

    /**
     * Phone number of user
     */
    private String phone;

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

    private String createUser;

    /**
     * update user
     */
    private String updateUser;

    private String lastProjectId;

    private String lastWorkspaceId;

    private String seleniumServer;

    private Boolean delFlag;

}
