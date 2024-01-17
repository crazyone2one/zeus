package cn.master.zeus.entity;

import com.mybatisflex.annotation.Table;
import java.io.Serializable;
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
@Table(value = "test_plan_follow")
public class TestPlanFollow implements Serializable {

    private String testPlanId;

    /**
     * 关注人
     */
    private String followId;

}
