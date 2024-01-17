package cn.master.zeus.dto.request.testcase;

import cn.master.zeus.dto.domain.ModuleNode;
import cn.master.zeus.entity.TestCaseNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Setter
@Getter
public class DragNodeRequest extends ModuleNode {
    List<String> nodeIds;
    TestCaseNode nodeTree;
}
