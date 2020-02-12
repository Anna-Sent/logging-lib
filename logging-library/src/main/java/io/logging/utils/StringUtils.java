package io.logging.utils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.Iterator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    public static boolean isEmpty(@Nullable CharSequence string) {
        return string == null || string.length() == 0;
    }

    public static boolean equals(@Nullable String s1, @Nullable String s2) {
        return isEmpty(s1) ? isEmpty(s2) : s1.equals(s2);
    }

    public static boolean startsWith(String string, String filter) {
        return string.toLowerCase().startsWith(filter.toLowerCase());
    }

    public static String stringOrEmpty(@Nullable String s) {
        return s == null ? "" : s;
    }

    @Nullable
    public static String trimOrNull(@Nullable String s) {
        return s == null ? null : s.trim();
    }

    @Nullable
    public static String removeSpacesOrNull(@Nullable String s) {
        return s == null ? null : s.replace(" ", "");
    }

    @Nullable
    public static String firstSignToUpperCase(@Nullable String text) {
        if (isEmpty(text)) {
            return text;
        }

        char[] chars = text.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    @Contract("null, null -> null; !null, null -> !null;"
            + " null, !null -> !null; !null, !null -> !null")
    @Nullable
    public static String concat(@Nullable String first, @Nullable String second) {
        return concat("\n", first, second);
    }

    @Nullable
    public static String concat(String delimiter, @Nullable String first, @Nullable String second) {
        if (StringUtils.isEmpty(first)) {
            return StringUtils.isEmpty(second) ? null : second;
        }
        return first + (StringUtils.isEmpty(second) ? "" : delimiter + second);
    }

    public static String join(CharSequence delimiter, Iterable tokens) {
        Iterator<?> iterator = tokens.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(iterator.next());
        while (iterator.hasNext()) {
            sb.append(delimiter);
            sb.append(iterator.next());
        }
        return sb.toString();
    }
}
