package cn.master.zeus.controller;

import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.service.ISystemGroupService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/systemGroup")
public class SystemGroupController {

    @Autowired
    private ISystemGroupService iSystemGroupService;

    /**
     * 添加。
     *
     * @param systemGroup 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody SystemGroup systemGroup) {
        return iSystemGroupService.save(systemGroup);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iSystemGroupService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param systemGroup 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemGroup systemGroup) {
        return iSystemGroupService.updateById(systemGroup);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemGroup> list() {
        return iSystemGroupService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public SystemGroup getInfo(@PathVariable Serializable id) {
        return iSystemGroupService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SystemGroup> page(Page<SystemGroup> page) {
        return iSystemGroupService.page(page);
    }

}
