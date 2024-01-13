package cn.master.zeus.controller;

import cn.master.zeus.dto.CustomFieldTemplateDao;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.service.ICustomFieldTemplateService;
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
@RequiredArgsConstructor
@RequestMapping("/custom/field/template")
public class CustomFieldTemplateController {

    private final ICustomFieldTemplateService iCustomFieldTemplateService;

    /**
     * 添加。
     *
     * @param customFieldTemplate 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CustomFieldTemplate customFieldTemplate) {
        return iCustomFieldTemplateService.save(customFieldTemplate);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iCustomFieldTemplateService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param customFieldTemplate 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CustomFieldTemplate customFieldTemplate) {
        return iCustomFieldTemplateService.updateById(customFieldTemplate);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @PostMapping("list")
    @PreAuthorize("hasAnyAuthority('PROJECT_TEMPLATE:READ+CUSTOM','PROJECT_TEMPLATE:READ+CASE_TEMPLATE','PROJECT_TEMPLATE:READ+ISSUE_TEMPLATE','PROJECT_TEMPLATE:READ+API_TEMPLATE')")
    public List<CustomFieldTemplateDao> list(@RequestBody CustomFieldTemplate request) {
        return iCustomFieldTemplateService.getList(request);
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public CustomFieldTemplate getInfo(@PathVariable Serializable id) {
        return iCustomFieldTemplateService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CustomFieldTemplate> page(Page<CustomFieldTemplate> page) {
        return iCustomFieldTemplateService.page(page);
    }

}
