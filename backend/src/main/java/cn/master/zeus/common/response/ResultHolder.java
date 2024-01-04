package cn.master.zeus.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
@Data
public class ResultHolder {
    // 请求是否成功
    private boolean success = false;
    // 描述信息
    private String message;
    // 返回数据
    private Object data = "";

    public ResultHolder() {
        this.success = true;
    }

    public ResultHolder(Object data) {
        this.data = data;
        this.success = true;
    }

    public ResultHolder(boolean success, String msg) {
        this.success = success;
        this.message = msg;
    }

    public ResultHolder(boolean success, String msg, Object data) {
        this.success = success;
        this.message = msg;
        this.data = data;
    }
    public static ResultHolder success(Object obj) {
        return new ResultHolder(obj);
    }

    public static ResultHolder error(String message) {
        return new ResultHolder(false, message, null);
    }

    public static ResultHolder error(String message, Object object) {
        return new ResultHolder(false, message, object);
    }
}
