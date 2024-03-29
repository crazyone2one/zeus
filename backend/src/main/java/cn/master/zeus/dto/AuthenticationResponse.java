package cn.master.zeus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 12/29/2023
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String accessToken;

    private String refreshToken;

    private UserDTO user;
    ;
}
