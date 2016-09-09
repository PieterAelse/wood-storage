package com.jordylangen.woodstorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class LogEntry {

    static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyMMddHHmmss", Locale.ENGLISH);

    static final String SEPARATOR = "``";
    static final String NEWLINE = "\n";
    static final String NEWLINE_REPLACEMENT = "~~";
    static final String NULL = "null";

    private Date timeStamp;
    private String tag;
    private int priority;
    private String message;

    public LogEntry(String tag, int priority, String message) {
        this(tag, priority, message, new Date());
    }

    private LogEntry(String tag, int priority, String message, Date timeStamp) {
        this.tag = tag;
        this.priority = priority;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getTag() {
        return tag;
    }

    public int getPriority() {
        return priority;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    String serialize() {
        return tag + SEPARATOR + priority + SEPARATOR + removeNewLines(message) + SEPARATOR + DATE_TIME_FORMAT.format(timeStamp);
    }

    static LogEntry deserialize(String line) {
        String[] values = line.split(SEPARATOR);

        String tag = values[0];
        int priority = Integer.parseInt(values[1]);
        String message = reAddNewLines(values[2]);
        Date timeStamp;
        try {
            timeStamp = DATE_TIME_FORMAT.parse(values[3]);
        } catch (ParseException e) {
            timeStamp = new Date();
        }

        return new LogEntry(NULL.equals(tag) ? null : tag, priority, NULL.equals(message) ? null : message, timeStamp);
    }

    private static String removeNewLines(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.replace(NEWLINE, NEWLINE_REPLACEMENT);
    }

    private static String reAddNewLines(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.replace(NEWLINE_REPLACEMENT, NEWLINE);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{tag, priority, message, timeStamp});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LogEntry)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        LogEntry other = (LogEntry) obj;

        return equals(tag, other.tag) &&
                equals(priority, other.priority) &&
                equals(message, other.message) &&
                equals(DATE_TIME_FORMAT.format(timeStamp), DATE_TIME_FORMAT.format(other.timeStamp));
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
