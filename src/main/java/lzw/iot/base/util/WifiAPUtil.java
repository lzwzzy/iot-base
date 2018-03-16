package lzw.iot.base.util;

import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.exception.LemonException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @author zzy
 * @date 2018/3/16 15:04
 **/
public class WifiAPUtil {

    private static final Log LOGGER = LogFactory.getLog(WifiAPUtil.class);

    public static void excuteShellScript(String script, String workspace, String... evnp) {
        try {
            String cmd = "sh " + script;
            File dir = null;
            if (workspace != null) {
                dir = new File(workspace);
                LOGGER.info(workspace);
            }
            Process process = Runtime.getRuntime().exec(cmd, evnp, dir);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                LOGGER.info(line);
            }
            input.close();
        } catch (Exception e) {
            throw new LemonException(e.getMessage());
        }
    }
}
