package io.logging.sample;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import io.logging.CrashReportSystem;
import io.logging.DumpingUncaughtExceptionHandler;
import io.logging.LogSystem;
import io.logging.utils.DeviceUtils;

@SuppressWarnings("WeakerAccess")
public class App extends Application {

    private Logger logger;

    @Override
    public void onCreate() {
        super.onCreate();

        // install uncaught exception handler before initializing crash report system
        initUncaughtExceptionHandler();
        initLogging();
    }

    private void initUncaughtExceptionHandler() {
        boolean dumpOutOfMemory = BuildConfig.DUMP_OUT_OF_MEMORY
                && DeviceUtils.isExternalStorageWritable();
        if (!dumpOutOfMemory) {
            return;
        }
        File externalDir = getExternalFilesDir(null);
        if (externalDir == null) {
            return;
        }
        String directoryPath = externalDir.getAbsolutePath();
        Thread.UncaughtExceptionHandler handler =
                new DumpingUncaughtExceptionHandler(directoryPath);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    private void initLogging() {
        LogSystem.initLogger(this, LogSystem.Config.builder()
                        .tag(BuildConfig.LOG_TAG)
                        .enableCrs(BuildConfig.ENABLE_CRS)
                        .printLogsToFile(BuildConfig.PRINT_LOGS_TO_FILE)
                        .printLogsToCrs(BuildConfig.PRINT_LOGS_TO_CRS)
                        .printLogsToLogcat(BuildConfig.PRINT_LOGS_TO_LOGCAT)
                        .printSensitiveData(BuildConfig.PRINT_SENSITIVE_DATA)
                        .build(),
                new CrashReportSystem() {
                    @Override
                    public void init(Context appContext, boolean enableCrs) {
                        if (BuildConfig.ENABLE_CRS) {
                            Fabric.with(appContext, new Crashlytics());
                        }
                    }

                    @Override
                    public void report(Throwable throwable) {
                        Crashlytics.logException(throwable);
                    }

                    @Override
                    public void log(String msg) {
                        Crashlytics.log(msg);
                    }
                });
        logger = LoggerFactory.getLogger(toString());
        logger.debug("onCreate");
    }
}
