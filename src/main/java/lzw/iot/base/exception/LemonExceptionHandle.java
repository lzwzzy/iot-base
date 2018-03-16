package lzw.iot.base.exception;

import lzw.iot.base.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zzy
 * @date 2018/3/16 15:50
 **/
@RestControllerAdvice
public class LemonExceptionHandle {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(LemonException.class)
    public R handleRRException(LemonException e){
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        logger.error(String.format("errorCode: %s\nerrorMsg: %s", e.getCode(), e.getMsg()));
        return r;
    }
}
