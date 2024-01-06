package cn.master.zeus.dto.request.user;

import cn.master.zeus.dto.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 01/05/2024
 **/
@Setter
@Getter
public class UserRequest extends BaseRequest {
    private String email;
}
