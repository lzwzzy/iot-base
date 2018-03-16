package lzw.iot.base;

import lzw.iot.base.util.WifiAPUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
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
                + "\n=========================================================" );
		SpringApplication.run(IotBaseApplication.class, args);

        Scanner in = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            LOGGER.info("hello");

            // get user input selection
            String selection = in.next();

            switch (selection.toUpperCase()) {
                case "O":
                    WifiAPUtil.excuteShellScript("start_AP", "./", "");
                    break;
                case "I":
                    in.close();
                    break;
                case "X":
                    exit = true;
                    break;
                default:
                    LOGGER.error("Invalid Entry, Try Again!");
                    break;
            }
        }

        in.close();
	}


}
