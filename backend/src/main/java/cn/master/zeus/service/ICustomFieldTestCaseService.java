package cn.master.zeus.service;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.CustomFieldResourceDTO;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.CustomFieldTestCase;

import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldTestCaseService extends IService<CustomFieldTestCase> {

    void addFields(String resourceId, List<CustomFieldResourceDTO> addFields);

    Map<String, List<CustomFieldDao>> getMapByResourceIdsForList(List<String> resourceIds);
}
