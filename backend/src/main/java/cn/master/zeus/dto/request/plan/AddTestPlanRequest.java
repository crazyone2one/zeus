package cn.master.zeus.dto.request.plan;

import cn.master.zeus.entity.TestPlan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Created by 11's papa on 01/13/2024
 **/
@Setter
@Getter
public class AddTestPlanRequest extends TestPlan {
    private List<String> projectIds;
    private List<String> principals;
    private List<String> follows;
}
