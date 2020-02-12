package io.logging;

import android.content.Context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import lombok.val;

class CrashReportSystemAppenderFactory extends AppenderFactory {

    private static final String LOG_PATTERN = "[%thread] %level %logger{0} %message%n";

    @Override
    public Appender<ILoggingEvent> getAppender(LoggerContext loggerContext, Context appContext) {
        val crashReportSystemAppender = new CrashReportSystemAppender();
        crashReportSystemAppender.setContext(loggerContext);

        crashReportSystemAppender.setEncoder(getPatternLayoutEncoder(loggerContext, LOG_PATTERN));

        crashReportSystemAppender.start();
        return crashReportSystemAppender;
    }
}
