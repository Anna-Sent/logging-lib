package io.logging;

import android.content.Context;

import java.io.File;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import lombok.val;

import static io.logging.utils.DeviceUtils.isExternalStorageWritable;

class FileAppenderFactory extends AppenderFactory {

    private static final int MAX_LOG_FILE_COUNT = 3;
    private static final int ROLLING_POLICY_MIN_INDEX = 1;
    private static final int ROLLING_POLICY_MAX_INDEX =
            ROLLING_POLICY_MIN_INDEX + MAX_LOG_FILE_COUNT - 1;
    private static final String MAX_LOG_SIZE = "1MB";
    private static final String LOG_PATTERN = "%date [%thread] %level %logger{0} %message%n";
    private static final String LOG_FILE_NAME = "log.txt";
    private static final String LOG_FILE_NAME_PATTERN = "log.%i.txt";

    @Override
    public Appender<ILoggingEvent> getAppender(LoggerContext loggerContext, Context appContext) {
        val filesDir = isExternalStorageWritable()
                ? appContext.getExternalFilesDir(null) : appContext.getFilesDir();
        //noinspection ConstantConditions
        return getAppender(loggerContext, filesDir);
    }

    private Appender<ILoggingEvent> getAppender(LoggerContext loggerContext, File filesDir) {
        val fileName = new File(filesDir, LOG_FILE_NAME).getAbsolutePath();
        val fineNamePattern = new File(filesDir, LOG_FILE_NAME_PATTERN).getAbsolutePath();

        val rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(loggerContext);

        rollingFileAppender.setFile(fileName);

        rollingFileAppender.setRollingPolicy(
                getRollingPolicy(loggerContext, rollingFileAppender, fineNamePattern));
        rollingFileAppender.setTriggeringPolicy(
                getTriggeringPolicy(loggerContext));

        rollingFileAppender.setEncoder(getPatternLayoutEncoder(loggerContext, LOG_PATTERN));

        rollingFileAppender.start();
        return rollingFileAppender;
    }

    private RollingPolicy getRollingPolicy(
            LoggerContext loggerContext,
            RollingFileAppender rollingFileAppender,
            String fileNamePattern) {
        val rollingPolicy = new FixedWindowRollingPolicy();

        rollingPolicy.setFileNamePattern(fileNamePattern);
        rollingPolicy.setMinIndex(ROLLING_POLICY_MIN_INDEX);
        rollingPolicy.setMaxIndex(ROLLING_POLICY_MAX_INDEX);

        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.start();
        return rollingPolicy;
    }

    private TriggeringPolicy<ILoggingEvent> getTriggeringPolicy(LoggerContext loggerContext) {
        val triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();

        triggeringPolicy.setMaxFileSize(FileSize.valueOf(MAX_LOG_SIZE));

        triggeringPolicy.setContext(loggerContext);
        triggeringPolicy.start();
        return triggeringPolicy;
    }
}
