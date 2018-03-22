package lzw.iot.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author zzy
 * @date 2018/3/16 15:04
 **/
public class ShellUtil {

    private static final Log LOGGER = LogFactory.getLog(ShellUtil.class);

    public static void excuteShellScript(String script, String workspace, String... evnp) throws IOException {

        String cmd = script;
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

    }

    public static String excuteCMD(String script) throws IOException {

        String cmd = script;
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String result = "";
        while ((line = input.readLine()) != null) {
            LOGGER.info(line);
            result = line + "\n";
        }
        input.close();
        return result;
    }
}
