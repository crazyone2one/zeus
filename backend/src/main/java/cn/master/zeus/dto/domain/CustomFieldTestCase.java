package cn.master.zeus.dto.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomFieldTestCase extends CustomFieldTestCaseKey implements Serializable {
    private String value;

    private String textValue;

    @Serial
    private static final long serialVersionUID = 1L;
}
