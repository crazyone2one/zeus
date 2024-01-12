package cn.master.zeus.controller;

import cn.master.zeus.dto.request.QueryCustomFieldRequest;
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
import cn.master.zeus.entity.CustomField;
import cn.master.zeus.service.ICustomFieldService;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

/**
 * 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/custom/field")
@RequiredArgsConstructor
public class CustomFieldController {
    private final ICustomFieldService iCustomFieldService;

    /**
     * 添加。
     *
     * @param customField
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CUSTOM')")
    public String save(@RequestBody CustomField customField) {
        return iCustomFieldService.add(customField);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CUSTOM')")
    public void remove(@PathVariable String id) {
        iCustomFieldService.delete(id);
    }

    /**
     * 根据主键更新。
     *
     * @param customField
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CUSTOM')")
    public void update(@RequestBody CustomField customField) {
         iCustomFieldService.updateCustomField(customField);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CustomField> list() {
        return iCustomFieldService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public CustomField getInfo(@PathVariable Serializable id) {
        return iCustomFieldService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CUSTOM')")
    public Page<CustomField> page(@RequestBody QueryCustomFieldRequest page) {
        return iCustomFieldService.getPage(page);
    }

    @PostMapping("/list/relate")
    @PreAuthorize("hasAuthority('PROJECT_TEMPLATE:READ+CUSTOM')")
    public Page<CustomField> listRelate(@RequestBody QueryCustomFieldRequest page) {
        return iCustomFieldService.listRelate(page);
    }

}
