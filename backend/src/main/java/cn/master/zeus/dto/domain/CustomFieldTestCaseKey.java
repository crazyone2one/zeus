package cn.master.zeus.dto.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 01/16/2024
 **/
@Data
public class CustomFieldTestCaseKey implements Serializable {
    private String resourceId;

    private String fieldId;

    @Serial
    private static final long serialVersionUID = 1L;
}
