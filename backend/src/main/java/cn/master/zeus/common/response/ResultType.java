package cn.master.zeus.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Getter
@AllArgsConstructor
public enum ResultType {
    SUCCESS(200, "操作成功"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");
    // 状态码和状态信息
    private final int code;
    private final String message;
}
