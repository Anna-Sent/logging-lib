package io.logging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import static io.logging.utils.StringUtils.concat;

@SuppressWarnings({"WeakerAccess", "unused"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogSystem {

    static CrashReportSystem crashReportSystem;

    private static Config config = Config.builder()
            .tag(LogSystem.class.getSimpleName())
            .build();

    @Contract("null -> null; !null -> !null")
    @Nullable
    public static String sens(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        return config.printSensitiveData ? o.toString() : "...";
    }

    public static void initLogger(Context appContext,
                                  Config config,
                                  @Nullable CrashReportSystem crashReportSystem) {
        LogSystem.config = config;
        LogSystem.crashReportSystem = crashReportSystem;
        if (crashReportSystem != null) {
            crashReportSystem.init(appContext, config.enableCrs);
        }

        val factories = new ArrayList<AppenderFactory>();
        if (config.printLogsToFile) {
            factories.add(new FileAppenderFactory());
        }
        if (config.printLogsToCrs) {
            factories.add(new CrashReportSystemAppenderFactory());
        }
        if (config.printLogsToLogcat) {
            factories.add(new LogcatAppenderFactory(config.tag));
        }

        val loggerContext = initLoggerContext();
        if (loggerContext == null) {
            return;
        }

        val root = initRoot();
        if (root == null) {
            return;
        }

        for (val factory : factories) {
            try {
                val appender = factory.getAppender(loggerContext, appContext);
                root.addAppender(appender);
            } catch (Exception e) {
                report(null, "failed to configure log " + factory, e);
            }
        }

        StatusPrinter.print(loggerContext);
    }

    @SuppressLint("LogNotTimber")
    public static void report(@Nullable Logger logger,
                              @Nullable String msg,
                              Throwable throwable) {
        if (logger == null) {
            if (config.printLogsToLogcat) {
                Log.e(config.tag, msg, throwable); // logcat appender
            }
            if (config.printLogsToCrs && crashReportSystem != null) {
                crashReportSystem.log(toMessage(msg, throwable)); // crash report system appender
            }
        } else {
            logger.error(msg, throwable);
        }

        if (crashReportSystem != null) {
            crashReportSystem.report(throwable);
        }
    }

    @Nullable
    private static LoggerContext initLoggerContext() {
        try {
            val loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            return loggerContext;
        } catch (ClassCastException e) {
            report(null, "failed to init logger context", e);
            return null;
        }
    }

    @Nullable
    private static ch.qos.logback.classic.Logger initRoot() {
        try {
            val root = (ch.qos.logback.classic.Logger) LoggerFactory
                    .getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.TRACE);
            return root;
        } catch (ClassCastException e) {
            report(null, "failed to init root", e);
            return null;
        }
    }

    private static String toMessage(@Nullable String msg, Throwable throwable) {
        String stackTraceString = Log.getStackTraceString(throwable);
        return concat(msg, stackTraceString);
    }

    @Value
    @Builder
    public static class Config {

        @NonNull
        String tag;

        boolean enableCrs;

        boolean printLogsToFile;

        boolean printLogsToCrs;

        boolean printLogsToLogcat;

        boolean printSensitiveData;
    }
}
