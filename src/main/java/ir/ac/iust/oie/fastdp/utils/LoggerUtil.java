package ir.ac.iust.oie.fastdp.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.IOException;

/**
 * Created by majid on 12/20/14.
 */
public class LoggerUtil {

    private static boolean initiated = false;

    public static void initLogger() {
        try {
            PatternLayout LOG_PATTERN = new PatternLayout("%5p %d{HH:mm:ss} - %m%n");
            Logger.getRootLogger().getAppender("stdout").setLayout(LOG_PATTERN);

            //add file appender
            String filePath = System.getProperty("user.dir") + File.separator + "fast-dp.log";
            RollingFileAppender appender = new RollingFileAppender(LOG_PATTERN, filePath);
            appender.setName("Fast DP Log");
            appender.setMaxFileSize("1MB");
            appender.activateOptions();
            Logger.getRootLogger().addAppender(appender);
            initiated = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger(Class clazz) {
        if (!initiated) initLogger();
        return Logger.getLogger(clazz);
    }
}
