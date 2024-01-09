package cn.master.zeus.dto;

import cn.master.zeus.entity.SystemGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 01/08/2024
 **/
@Setter
@Getter
public class GroupDTO extends SystemGroup {
    private String scopeName;
    private Integer memberSize;
}
