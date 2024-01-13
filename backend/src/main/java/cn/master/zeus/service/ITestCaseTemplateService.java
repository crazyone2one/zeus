package cn.master.zeus.service;

import cn.master.zeus.dto.CustomFieldDao;
import cn.master.zeus.dto.TestCaseTemplateDao;
import cn.master.zeus.dto.request.BaseRequest;
import cn.master.zeus.dto.request.UpdateCaseFieldTemplateRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.zeus.entity.TestCaseTemplate;

import java.util.List;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ITestCaseTemplateService extends IService<TestCaseTemplate> {

    Page<TestCaseTemplate> getPage(BaseRequest request);

    String add(UpdateCaseFieldTemplateRequest request);

    void updateTestCaseTemplate(UpdateCaseFieldTemplateRequest request);

    void delete(String id);

    List<TestCaseTemplate> getOption(String projectId);

    TestCaseTemplateDao getTemplate(String projectId);

    void handleSystemFieldCreate(CustomFieldDao customFieldDao);

    TestCaseTemplateDao getTemplateForList(String projectId);
}
