package io.logging;

import android.content.Context;

@SuppressWarnings("WeakerAccess")
public interface CrashReportSystem {

    void init(Context appContext, boolean enableCrs);

    void report(Throwable throwable);

    void log(String msg);
}
