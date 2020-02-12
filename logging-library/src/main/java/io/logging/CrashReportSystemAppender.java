package io.logging;

import androidx.annotation.Nullable;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class CrashReportSystemAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Nullable
    @Setter
    private PatternLayoutEncoder encoder;

    @Override
    public void start() {
        if (encoder == null || encoder.getLayout() == null) {
            addError("No layout set for the appender " + toString());
            return;
        }

        super.start();
    }

    @Override
    public void append(ILoggingEvent event) {
        if (!started || encoder == null) {
            return;
        }

        String msg = encoder.getLayout().doLayout(event);
        LogSystem.crashReportSystem.log(msg);
    }
}
