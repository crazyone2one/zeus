package cn.master.zeus.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * token信息表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "system_token")
public class SystemToken implements Serializable {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * token
     */
    private String token;

    /**
     * token type
     */
    private String tokenType;

    private Boolean revoked;

    /**
     * user id
     */
    private String userId;

}
