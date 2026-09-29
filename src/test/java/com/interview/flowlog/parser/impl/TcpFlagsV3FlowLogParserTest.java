package com.interview.flowlog.parser.impl;

import com.interview.flowlog.model.FlowLogRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class TcpFlagsV3FlowLogParserTest {
    private final TcpFlagsV3FlowLogParser parser = new TcpFlagsV3FlowLogParser();

    @Test void parsesTcpFlagsV3Record() {
        String line = "3 vpc-abcdefab012345678 subnet-aaaaaaaa012345678 i-01234567890123456 " +
                "eni-1235b8ca123456789 123456789010 IPv4 52.213.180.42 10.0.0.62 43416 5001 " +
                "52.213.180.42 10.0.0.62 6 568 8 1566848875 1566848933 ACCEPT 2 OK";
        FlowLogRecord r = parser.parse(line);
        assertEquals(3, r.version());
        assertEquals("123456789010", r.accountId());
        assertEquals("eni-1235b8ca123456789", r.interfaceId());
        assertEquals("52.213.180.42", r.sourceIp());
        assertEquals("10.0.0.62", r.destinationIp());
        assertEquals(43416, r.sourcePort());
        assertEquals(5001, r.destinationPort());
        assertEquals(6, r.protocol());
        assertEquals(8, r.packets());
        assertEquals(568, r.bytes());
        assertEquals("ACCEPT", r.action());
        assertEquals("OK", r.logStatus());
    }

    @Test void rejectsWrongFieldCount() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("3 too few fields"));
    }

    @Test void rejectsInvalidNumericField() {
        String line = "3 vpc-abcdefab012345678 subnet-aaaaaaaa012345678 i-01234567890123456 " +
                "eni-1235b8ca123456789 123456789010 IPv4 52.213.180.42 10.0.0.62 BAD 5001 " +
                "52.213.180.42 10.0.0.62 6 568 8 1566848875 1566848933 ACCEPT 2 OK";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test void rejectsNonIpv4Address() {
        String line = "3 vpc-abcdefab012345678 subnet-aaaaaaaa012345678 i-01234567890123456 " +
                "eni-1235b8ca123456789 123456789010 IPv4 2001:db8::1 10.0.0.62 43416 5001 " +
                "52.213.180.42 10.0.0.62 6 568 8 1566848875 1566848933 ACCEPT 2 OK";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rejectsNullOrBlankLine(String line) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}

