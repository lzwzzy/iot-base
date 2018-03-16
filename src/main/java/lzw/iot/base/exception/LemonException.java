package lzw.iot.base.exception;

import lzw.iot.base.common.ErrorCode;

/**
 *  自定义异常类
 * @author lzw
 * @date 2018/3/16 15:14
 **/
public class LemonException extends RuntimeException {
    /**
     * 错误信息
     */
    private String msg;

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误枚举
     */
    private ErrorCode.System systemError;


    public LemonException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public LemonException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public LemonException(Throwable cause, String msg, String code) {
        super(msg, cause);
        this.msg = msg;
        this.code = code;
    }

    public LemonException(Throwable cause, String msg) {
        super(msg, cause);
        this.msg = msg;
    }

    public LemonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String msg, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.msg = msg;
        this.code = code;
    }

    public LemonException(Throwable cause, ErrorCode.System systemError) {
        super(systemError.getMessage(), cause);
        this.systemError = systemError;
    }

    public LemonException(ErrorCode.System systemError) {
        super(systemError.getMessage());
        this.systemError = systemError;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ErrorCode.System getSystemError() {
        return systemError;
    }

    public void setSystemError(ErrorCode.System systemError) {
        this.systemError = systemError;
    }
}
