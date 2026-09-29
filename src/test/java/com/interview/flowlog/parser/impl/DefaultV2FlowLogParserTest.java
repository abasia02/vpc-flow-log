package com.interview.flowlog.parser.impl;

import com.interview.flowlog.model.FlowLogRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class DefaultV2FlowLogParserTest {
    private final DefaultV2FlowLogParser parser = new DefaultV2FlowLogParser();

    @Test void parsesDefaultV2Record() {
        String line = "2 123456789010 eni-abc123 10.0.0.1 10.0.0.2 49153 443 6 10 840 1620140661 1620140721 ACCEPT OK";
        FlowLogRecord r = parser.parse(line);
        assertEquals("10.0.0.1", r.sourceIp());
        assertEquals("10.0.0.2", r.destinationIp());
        assertEquals(49153, r.sourcePort());
        assertEquals(443, r.destinationPort());
        assertEquals(6, r.protocol());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2 too few fields", "2 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 too many fields"})
    void rejectsWrongFieldCount(String line) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test void rejectsInvalidNumericField() {
        String line = "2 123 eni-x 10.0.0.1 10.0.0.2 BAD 443 6 1 2 3 4 ACCEPT OK";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test void rejectsNonIpv4Address() {
        String line = "2 123456789010 eni-abc123 2001:db8::1 10.0.0.2 49153 443 6 10 840 1620140661 1620140721 ACCEPT OK";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rejectsNullOrBlankLine(String line) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}

