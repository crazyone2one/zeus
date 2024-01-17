package cn.master.zeus.service.impl;

import cn.master.zeus.entity.TestCaseNode;
import cn.master.zeus.entity.TestPlanNode;
import cn.master.zeus.service.BuildTreeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Service
public class BuildTreeServiceImpl implements BuildTreeService {
    @Override
    public List<TestCaseNode> getTestCaseNodeTrees(List<TestCaseNode> nodes, Map<String, Integer> countMap) {
        List<TestCaseNode> nodeTreeList = new ArrayList<>();
        Map<Integer, List<TestCaseNode>> nodeLevelMap = new HashMap<>();
        nodes.forEach(node -> {
            Integer level = node.getLevel();
            if (nodeLevelMap.containsKey(level)) {
                nodeLevelMap.get(level).add(node);
            } else {
                List<TestCaseNode> testCaseNodes = new ArrayList<>();
                testCaseNodes.add(node);
                nodeLevelMap.put(node.getLevel(), testCaseNodes);
            }
        });
        List<TestCaseNode> rootNodes = Optional.ofNullable(nodeLevelMap.get(1)).orElse(new ArrayList<>());
        rootNodes.forEach(rootNode ->
                nodeTreeList.add(buildTestCaseNodeTree(nodeLevelMap, rootNode, countMap)));
        return nodeTreeList;
    }

    @Override
    public List<TestPlanNode> getTestPlanNodeTrees(List<TestPlanNode> nodes, Map<String, Integer> countMap) {
        List<TestPlanNode> nodeTreeList = new ArrayList<>();
        Map<Integer, List<TestPlanNode>> nodeLevelMap = new HashMap<>();
        nodes.forEach(node -> {
            Integer level = node.getLevel();
            if (nodeLevelMap.containsKey(level)) {
                nodeLevelMap.get(level).add(node);
            } else {
                List<TestPlanNode> testCaseNodes = new ArrayList<>();
                testCaseNodes.add(node);
                nodeLevelMap.put(node.getLevel(), testCaseNodes);
            }
        });
        List<TestPlanNode> rootNodes = Optional.ofNullable(nodeLevelMap.get(1)).orElse(new ArrayList<>());
        rootNodes.forEach(rootNode ->
                nodeTreeList.add(buildTestPlanNodeTree(nodeLevelMap, rootNode, countMap)));
        return nodeTreeList;
    }

    @Override
    public Map<String, Integer> getCountMap(List<TestCaseNode> nodes) {
        return nodes.stream().collect(Collectors.toMap(TestCaseNode::getId, TestCaseNode::getCaseNum));
    }

    private TestPlanNode buildTestPlanNodeTree(Map<Integer, List<TestPlanNode>> nodeLevelMap, TestPlanNode rootNode, Map<String, Integer> countMap){
        TestPlanNode nodeTree = new TestPlanNode();
        BeanUtils.copyProperties(rootNode, nodeTree);
        nodeTree.setLabel(rootNode.getName());
        setTestPlanCaseNum(countMap, nodeTree);
        List<TestPlanNode> lowerNodes = nodeLevelMap.get(rootNode.getLevel() + 1);
        if (lowerNodes == null) {
            return nodeTree;
        }
        List<TestPlanNode> children = new ArrayList<>();
        lowerNodes.forEach(node -> {
            if (node.getParentId() != null && node.getParentId().equals(rootNode.getId())) {
                children.add(buildTestPlanNodeTree(nodeLevelMap, node, countMap));
                nodeTree.setChildren(children);
            }
        });
        if (countMap != null && CollectionUtils.isNotEmpty(children)) {
            Integer childrenCount = children.stream().map(TestPlanNode::getCaseNum).reduce(Integer::sum).get();
            nodeTree.setCaseNum(nodeTree.getCaseNum() + childrenCount);
        }
        return nodeTree;
    }
    private TestCaseNode buildTestCaseNodeTree(Map<Integer, List<TestCaseNode>>nodeLevelMap, TestCaseNode rootNode, Map<String, Integer> countMap) {
        TestCaseNode nodeTree = new TestCaseNode();
        BeanUtils.copyProperties(rootNode, nodeTree);
        nodeTree.setLabel(rootNode.getName());
        setTestCaseCaseNum(countMap, nodeTree);
        List<TestCaseNode> lowerNodes = nodeLevelMap.get(rootNode.getLevel() + 1);
        if (lowerNodes == null) {
            return nodeTree;
        }
        List<TestCaseNode> children = new ArrayList<>();
        lowerNodes.forEach(node -> {
            if (node.getParentId() != null && node.getParentId().equals(rootNode.getId())) {
                children.add(buildTestCaseNodeTree(nodeLevelMap, node, countMap));
                nodeTree.setChildren(children);
            }
        });
        if (countMap != null && CollectionUtils.isNotEmpty(children)) {
            Integer childrenCount = children.stream().map(TestCaseNode::getCaseNum).reduce(Integer::sum).get();
            nodeTree.setCaseNum(nodeTree.getCaseNum() + childrenCount);
        }
        return nodeTree;
    }

    private void setTestPlanCaseNum(Map<String, Integer> countMap, TestPlanNode nodeTree) {
        if (Objects.nonNull(countMap)) {
            if (Objects.nonNull(countMap.get(nodeTree.getId()))) {
                nodeTree.setCaseNum(countMap.get(nodeTree.getId()));
            } else {
                nodeTree.setCaseNum(0);
            }
        }
    }
    private void setTestCaseCaseNum(Map<String, Integer> countMap, TestCaseNode nodeTree) {
        if (Objects.nonNull(countMap)) {
            if (Objects.nonNull(countMap.get(nodeTree.getId()))) {
                nodeTree.setCaseNum(countMap.get(nodeTree.getId()));
            } else {
                nodeTree.setCaseNum(0);
            }
        }
    }
}
