package cn.master.zeus.dto.request.member;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 01/17/2024
 **/
@Setter
@Getter
public class EditPasswordRequest {
    private String newPassword;
    private String password;
    private String id;
}
