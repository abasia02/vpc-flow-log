package com.interview.flowlog.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FlowLogRecordTest {
    private static FlowLogRecord recordWith(String sourceIp, String destinationIp) {
        return new FlowLogRecord(2, "123456789010", "eni-abc", sourceIp, destinationIp,
                49153, 443, 6, 10, 840, 1620140661L, 1620140721L, "ACCEPT", "OK", "original line");
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.0.0.1", "0.0.0.0", "255.255.255.255", "192.168.1.1", "1.1.1.1"})
    void acceptsValidIpv4Addresses(String ip) {
        assertDoesNotThrow(() -> recordWith(ip, "10.0.0.2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2001:db8:1234:a100:8d6e:3477:df66:f105", // IPv6
            "256.1.1.1",                               // octet out of range
            "10.0.0",                                  // too few octets
            "10.0.0.1.5",                               // too many octets
            "10.0.0.01",                               // leading zero
            "not-an-ip"                                 // garbage
    })
    void rejectsNonIpv4SourceIp(String badIp) {
        assertThrows(IllegalArgumentException.class, () -> recordWith(badIp, "10.0.0.2"));
    }

    @Test void rejectsNonIpv4DestinationIp() {
        assertThrows(IllegalArgumentException.class, () -> recordWith("10.0.0.1", "bad-ip"));
    }

    @Test void rejectsNullIp() {
        assertThrows(IllegalArgumentException.class, () -> recordWith(null, "10.0.0.2"));
    }
}
