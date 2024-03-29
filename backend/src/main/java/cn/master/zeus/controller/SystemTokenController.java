package cn.master.zeus.controller;

import cn.master.zeus.entity.SystemToken;
import cn.master.zeus.service.ISystemTokenService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * token信息表 控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/systemToken")
public class SystemTokenController {

    @Autowired
    private ISystemTokenService iSystemTokenService;

    /**
     * 添加token信息表。
     *
     * @param systemToken token信息表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody SystemToken systemToken) {
        return iSystemTokenService.save(systemToken);
    }

    /**
     * 根据主键删除token信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return iSystemTokenService.removeById(id);
    }

    /**
     * 根据主键更新token信息表。
     *
     * @param systemToken token信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemToken systemToken) {
        return iSystemTokenService.updateById(systemToken);
    }

    /**
     * 查询所有token信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemToken> list() {
        return iSystemTokenService.list();
    }

    /**
     * 根据token信息表主键获取详细信息。
     *
     * @param id token信息表主键
     * @return token信息表详情
     */
    @GetMapping("getInfo/{id}")
    public SystemToken getInfo(@PathVariable Serializable id) {
        return iSystemTokenService.getById(id);
    }

    /**
     * 分页查询token信息表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SystemToken> page(Page<SystemToken> page) {
        return iSystemTokenService.page(page);
    }

}
