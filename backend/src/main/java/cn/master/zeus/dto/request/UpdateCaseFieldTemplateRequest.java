package cn.master.zeus.dto.request;

import cn.master.zeus.entity.CustomFieldTemplate;
import cn.master.zeus.entity.TestCaseTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 01/12/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCaseFieldTemplateRequest extends TestCaseTemplate {
    List<CustomFieldTemplate> customFields;
}
