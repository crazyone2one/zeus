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
import cn.master.zeus.entity.TestPlanApiScenario;
import cn.master.zeus.service.ITestPlanApiScenarioService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/testPlanApiScenario")
public class TestPlanApiScenarioController {

    @Autowired
    private ITestPlanApiScenarioService iTestPlanApiScenarioService;

    /**
     * 添加。
     *
     * @param testPlanApiScenario 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody TestPlanApiScenario testPlanApiScenario) {
        return iTestPlanApiScenarioService.save(testPlanApiScenario);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTestPlanApiScenarioService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param testPlanApiScenario 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TestPlanApiScenario testPlanApiScenario) {
        return iTestPlanApiScenarioService.updateById(testPlanApiScenario);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlanApiScenario> list() {
        return iTestPlanApiScenarioService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlanApiScenario getInfo(@PathVariable Serializable id) {
        return iTestPlanApiScenarioService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlanApiScenario> page(Page<TestPlanApiScenario> page) {
        return iTestPlanApiScenarioService.page(page);
    }

}
