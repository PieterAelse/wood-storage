package com.jordylangen.woodstorage

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale

class LogEntry private constructor(val tag: String?, val priority: Int, val message: String?, val timeStamp: Date) {

    constructor(tag: String, priority: Int, message: String) : this(tag, priority, message, Date())

    companion object {
        private val DATE_TIME_FORMAT = SimpleDateFormat("yyMMddHHmmss", Locale.ENGLISH)

        private const val SEPARATOR = "``"
        private const val NEWLINE = "\n"
        private const val NEWLINE_REPLACEMENT = "~~"
        private const val NULL = "null"

        @JvmStatic
        fun deserialize(line: String): LogEntry {
            val values = line.split(SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val tag = values[0]
            val priority = Integer.parseInt(values[1])
            val message = reAddNewLines(values[2])
            val timeStamp = try {
                DATE_TIME_FORMAT.parse(values[3])
            } catch (e: ParseException) {
                Date()
            }

            return LogEntry(if (NULL == tag) null else tag, priority, if (NULL == message) null else message, timeStamp)
        }

        private fun removeNewLines(value: String?): String? {
            return if (value == null || value.isEmpty()) {
                value
            } else value.replace(NEWLINE, NEWLINE_REPLACEMENT)

        }

        private fun reAddNewLines(value: String?): String? {
            return if (value == null || value.isEmpty()) {
                value
            } else value.replace(NEWLINE_REPLACEMENT, NEWLINE)

        }

        private fun equals(a: Any?, b: Any?): Boolean {
            return a != null && a == b
        }
    }

    fun serialize(): String {
        return tag + SEPARATOR + priority + SEPARATOR + removeNewLines(message) + SEPARATOR + DATE_TIME_FORMAT.format(
            timeStamp
        )
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(arrayOf(tag, priority, message, timeStamp))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LogEntry) {
            return false
        }

        if (this === other) {
            return true
        }

        val otherEntry = other as LogEntry?

        return equals(tag, otherEntry?.tag) &&
                equals(priority, otherEntry?.priority) &&
                equals(message, otherEntry?.message) &&
                equals(DATE_TIME_FORMAT.format(timeStamp), DATE_TIME_FORMAT.format(otherEntry?.timeStamp))
    }
}
