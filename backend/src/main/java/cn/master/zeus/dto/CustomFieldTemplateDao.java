package cn.master.zeus.dto;

import cn.master.zeus.entity.CustomFieldTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 01/13/2024
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomFieldTemplateDao extends CustomFieldTemplate {
    private String name;

    private String scene;

    private String type;

    private String remark;

    private String options;

    private Boolean system;
}
