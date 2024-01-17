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
import cn.master.zeus.entity.TestPlanPrincipal;
import cn.master.zeus.service.ITestPlanPrincipalService;
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
@RequestMapping("/testPlanPrincipal")
public class TestPlanPrincipalController {

    @Autowired
    private ITestPlanPrincipalService iTestPlanPrincipalService;

    /**
     * 添加。
     *
     * @param testPlanPrincipal 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody TestPlanPrincipal testPlanPrincipal) {
        return iTestPlanPrincipalService.save(testPlanPrincipal);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iTestPlanPrincipalService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param testPlanPrincipal 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody TestPlanPrincipal testPlanPrincipal) {
        return iTestPlanPrincipalService.updateById(testPlanPrincipal);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<TestPlanPrincipal> list() {
        return iTestPlanPrincipalService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public TestPlanPrincipal getInfo(@PathVariable Serializable id) {
        return iTestPlanPrincipalService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<TestPlanPrincipal> page(Page<TestPlanPrincipal> page) {
        return iTestPlanPrincipalService.page(page);
    }

}
