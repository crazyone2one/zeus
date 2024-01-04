package cn.master.zeus.common.exception;

/**
 * @author Created by 11's papa on 01/03/2024
 **/
public class BusinessException extends RuntimeException {
    private Object detail;

    private BusinessException(String message) {
        super(message);
    }

    private BusinessException(String message, Object detail) {
        super(message);
        this.detail = detail;
    }

    private BusinessException(Throwable cause) {
        super(cause);
    }

    private BusinessException(Throwable cause, Object detail) {
        super(cause);
        this.detail = detail;
    }

    public static void throwException(String message) {
        throw new BusinessException(message);
    }

    public static void throwException(String message, Object detail) {
        throw new BusinessException(message, detail);
    }

    public static BusinessException getException(String message) {
        throw new BusinessException(message);
    }

    public static void throwException(Throwable t) {
        throw new BusinessException(t);
    }

    public static void throwException(Throwable t, Object detail) {
        throw new BusinessException(t, detail);
    }
}
