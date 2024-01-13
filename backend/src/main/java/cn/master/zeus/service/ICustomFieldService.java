package cn.master.zeus.service;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.request.QueryCustomFieldRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.CustomField;

import java.io.Serializable;
import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ICustomFieldService extends IService<CustomField> {

    String add(CustomField customField);

    Page<CustomField> getPage(QueryCustomFieldRequest page);

    Page<CustomField> listRelate(QueryCustomFieldRequest page);

    void delete(String id);

    void updateCustomField(CustomField customField);

    List<CustomFieldDao> getCustomFieldByTemplateId(String id);

    List<CustomField> getFieldByIds(List<String> ids);

    List<CustomField> getDefaultField(QueryCustomFieldRequest request);
}
