## logback ##
## https://github.com/tony19/logback-android/wiki#proguard ##

-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-keepattributes *Annotation*

-dontwarn ch.qos.logback.core.net.*
