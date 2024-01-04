package cn.master.zeus.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Data
public class RefreshTokenRequest {
    @NotNull(message = "Refresh token is required")
    private String refreshToken;
}
