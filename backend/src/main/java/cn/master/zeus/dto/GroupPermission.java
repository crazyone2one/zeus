package cn.master.zeus.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Data
public class GroupPermission implements Serializable {
    private String id;
    private String name;
    private String resourceId;
    private Boolean checked = false;
    private Boolean license = false;
    @Serial
    private static final long serialVersionUID = 1L;
}
