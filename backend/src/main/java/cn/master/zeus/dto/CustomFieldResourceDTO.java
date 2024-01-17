package cn.master.zeus.dto;

import cn.master.zeus.dto.domain.CustomFieldTestCase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 自定义字段关联表的统一操作类.字段跟 CustomFieldIssues， CustomFieldTestCase 一样
 * @author Created by 11's papa on 01/16/2024
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomFieldResourceDTO extends CustomFieldTestCase implements Serializable {
    private String name;
    private String type;
    private String optionLabel;
    @Serial
    private static final long serialVersionUID = 1L;
}
