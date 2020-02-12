package io.logging;

import android.content.Context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import lombok.val;

class LogcatAppenderFactory extends AppenderFactory {

    private static final String LOG_PATTERN = "%message%n";

    private final String tagPattern;

    LogcatAppenderFactory(String tag) {
        tagPattern = tag + " %logger{0}";
    }

    @Override
    public Appender<ILoggingEvent> getAppender(LoggerContext loggerContext, Context appContext) {
        val logcatAppender = new LogcatAppender();
        logcatAppender.setContext(loggerContext);

        logcatAppender.setEncoder(getPatternLayoutEncoder(loggerContext, LOG_PATTERN));
        logcatAppender.setTagEncoder(getPatternLayoutEncoder(loggerContext, tagPattern));

        logcatAppender.start();
        return logcatAppender;
    }
}
