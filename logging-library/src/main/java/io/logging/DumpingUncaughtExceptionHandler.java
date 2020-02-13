package io.logging;

import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Locale;

public class DumpingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String DUMP_FILE_PREFIX = "dump_";
    private static final String DUMP_FILE_SUFFIX = ".hprof";
    private static final String FILE_NAME_FORMAT = DUMP_FILE_PREFIX + "%d" + DUMP_FILE_SUFFIX;

    private final String directoryPath;

    public DumpingUncaughtExceptionHandler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        if (isOutOfMemory(throwable)) {
            dumpHprof();
        }

        System.exit(1);
    }

    private boolean isOutOfMemory(@Nullable Throwable throwable) {
        return throwable != null
                && (throwable instanceof OutOfMemoryError
                || throwable != throwable.getCause()
                && throwable.getCause() != null
                && isOutOfMemory(throwable.getCause()));
    }

    private void dumpHprof() {
        String fileName = String.format(Locale.US, FILE_NAME_FORMAT, System.currentTimeMillis());
        File file = new File(directoryPath, fileName);
        String path = file.getAbsolutePath();
        dumpHprof(path);
    }

    private void dumpHprof(String path) {
        try {
            Debug.dumpHprofData(path);
        } catch (Throwable e) {
            Log.e("DUMP", "Failed to dump hprof data", e);
        }
    }
}
