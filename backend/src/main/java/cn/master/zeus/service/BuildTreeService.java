package cn.master.zeus.service;

import cn.master.zeus.entity.TestCaseNode;
import cn.master.zeus.entity.TestPlanNode;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
public interface BuildTreeService {

    List<TestCaseNode> getTestCaseNodeTrees(List<TestCaseNode> nodes, Map<String, Integer> countMap);

    List<TestPlanNode> getTestPlanNodeTrees(List<TestPlanNode> nodes, Map<String, Integer> countMap);

    Map<String, Integer> getCountMap(List<TestCaseNode> nodes);
}
