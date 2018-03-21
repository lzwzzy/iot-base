package lzw.iot.base;

import lzw.iot.base.service.impl.AsyncTaskServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class IotBaseApplication {

    private static final Log LOGGER = LogFactory.getLog(IotBaseApplication.class);



    public static void main(String[] args) {
        LOGGER.info("\n========================================================="
                + "\n                                                         "
                + "\n          欢迎来到柠檬IOT                                  "
                + "\n                                                         "
                + "\n    本程序为柠檬IOT多功能网关系统                            "
                + "\n    gitHub: https://github.com/lzwzzy/iot-base           "
                + "\n                                                         "
                + "\n=========================================================");
        SpringApplication.run(IotBaseApplication.class, args);

        AsyncTaskServiceImpl asyncTaskService = new AsyncTaskServiceImpl();
        asyncTaskService.gpioListenerTask();
    }





}
