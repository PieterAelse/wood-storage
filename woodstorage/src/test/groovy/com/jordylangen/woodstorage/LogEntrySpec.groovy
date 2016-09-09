package com.jordylangen.woodstorage

class LogEntrySpec extends RxSpecification {

    def "should remove newlines when serializing and add them again upon deserialization"() {
        given:
        def logEntry = new LogEntry("tag", 1, "this is\na message\ncontaining\nnew lines")

        when:
        def serialized = logEntry.serialize()

        then:
        !serialized.contains("\n")
        serialized.contains(LogEntry.NEWLINE_REPLACEMENT)

        when:
        def entry = LogEntry.deserialize(serialized)

        then:
        entry.message.contains("\n")
        !entry.message.contains(LogEntry.NEWLINE_REPLACEMENT)
    }
}