package cn.master.zeus.dto.request.user;

import cn.master.zeus.entity.SystemGroup;
import cn.master.zeus.entity.SystemUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Created by 11's papa on 01/05/2024
 **/
@Setter
@Getter
public class SystemUserDTO extends SystemUser {
    private List<SystemGroup> groups;
}
