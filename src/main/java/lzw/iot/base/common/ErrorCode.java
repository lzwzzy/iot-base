package lzw.iot.base.common;

/**
 * @author zzy
 * @date 2018/3/16 15:20
 **/
public enum ErrorCode {
    ;

    public interface CodeMessageEnum {
        String getMessage();

        String getCode();
    }


    /**
     * systemError
     */
    public enum System implements CodeMessageEnum {
        /**
         *  系统级错误
         */
        SYSTEM_ERROR("1005", "Internal Server Error"),
        THREAD_INTERRUPTION("1006", "线程中断异常");

        private String code;
        private String message;

        System(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public String getCode() {
            return this.code;
        }
    }
}
