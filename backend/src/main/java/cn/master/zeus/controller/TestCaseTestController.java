package cn.master.zeus.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.TestCaseTest;
import cn.master.zeus.service.ITestCaseTestService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 测试用例和关联用例的关系表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/testCaseTest")
public class TestCaseTestController {

    @Autowired
    private ITestCaseTestService iTestCaseTestService;

    /**
     * 添加测试用例和关联用例的关系表。
     *
     * @param testCaseTest 测试用例和关联用例的关系表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody TestCaseTest testCaseTest) {
        return iTestCaseTestService.save(testCaseTest);
    }

    /**
     * 根据主键删除测试用例和关联用例的关系表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTestCaseTestService.removeById(id);
    }

    /**
     * 根据主键更新测试用例和关联用例的关系表。
     *
     * @param testCaseTest 测试用例和关联用例的关系表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TestCaseTest testCaseTest) {
        return iTestCaseTestService.updateById(testCaseTest);
    }

    /**
     * 查询所有测试用例和关联用例的关系表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestCaseTest> list() {
        return iTestCaseTestService.list();
    }

    /**
     * 根据测试用例和关联用例的关系表主键获取详细信息。
     *
     * @param id 测试用例和关联用例的关系表主键
     * @return 测试用例和关联用例的关系表详情
     */
    @GetMapping("getInfo/{id}")
    public TestCaseTest getInfo(@PathVariable Serializable id) {
        return iTestCaseTestService.getById(id);
    }

    /**
     * 分页查询测试用例和关联用例的关系表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestCaseTest> page(Page<TestCaseTest> page) {
        return iTestCaseTestService.page(page);
    }

}
