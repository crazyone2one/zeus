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
import cn.master.zeus.entity.CustomFieldTestCase;
import cn.master.zeus.service.ICustomFieldTestCaseService;
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
@RequestMapping("/customFieldTestCase")
public class CustomFieldTestCaseController {

    @Autowired
    private ICustomFieldTestCaseService iCustomFieldTestCaseService;

    /**
     * 添加。
     *
     * @param customFieldTestCase 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CustomFieldTestCase customFieldTestCase) {
        return iCustomFieldTestCaseService.save(customFieldTestCase);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCustomFieldTestCaseService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param customFieldTestCase 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CustomFieldTestCase customFieldTestCase) {
        return iCustomFieldTestCaseService.updateById(customFieldTestCase);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CustomFieldTestCase> list() {
        return iCustomFieldTestCaseService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public CustomFieldTestCase getInfo(@PathVariable Serializable id) {
        return iCustomFieldTestCaseService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CustomFieldTestCase> page(Page<CustomFieldTestCase> page) {
        return iCustomFieldTestCaseService.page(page);
    }

}
