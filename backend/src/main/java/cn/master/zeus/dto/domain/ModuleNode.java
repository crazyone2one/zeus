package cn.master.zeus.dto.domain;

import cn.master.zeus.entity.TestCaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ModuleNode extends TestCaseNode {
    private String protocol;
    private String modulePath;
    private String scenarioType;
}
