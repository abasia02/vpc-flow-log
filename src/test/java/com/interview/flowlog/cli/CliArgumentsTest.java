package com.interview.flowlog.cli;

import com.interview.flowlog.parser.FlowLogFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CliArgumentsTest {
    @Test void parsesRequiredInputAndDefaultsFormatToV2() {
        Optional<CliArguments> parsed = CliArguments.parse(new String[]{"--input", "file.txt"});
        assertTrue(parsed.isPresent());
        assertEquals("file.txt", parsed.get().input());
        assertEquals(FlowLogFormat.DEFAULT_V2, parsed.get().format());
        assertFalse(parsed.get().showCounts());
    }

    @ParameterizedTest
    @CsvSource({"v2, DEFAULT_V2", "v3, V3_TCP_FLAGS"})
    void parsesExplicitFormat(String cliValue, FlowLogFormat expected) {
        Optional<CliArguments> parsed = CliArguments.parse(new String[]{"--input", "file.txt", "--format", cliValue});
        assertEquals(expected, parsed.get().format());
    }

    @Test void parsesFilterCriteria() {
        Optional<CliArguments> parsed = CliArguments.parse(new String[]{
                "--input", "file.txt", "--src-ip", "10.0.0.1", "--dst-port", "443"});
        assertEquals(Optional.of("10.0.0.1"), parsed.get().criteria().sourceIp());
        assertEquals(Optional.of(443), parsed.get().criteria().destinationPort());
    }

    @Test void detectsShowCountsFlag() {
        Optional<CliArguments> parsed = CliArguments.parse(new String[]{"--input", "file.txt", "--show-counts"});
        assertTrue(parsed.get().showCounts());
    }

    @Test void returnsEmptyWhenInputMissing() {
        assertTrue(CliArguments.parse(new String[]{"--format", "v2"}).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"bogus", "V2", "", "1"})
    void rejectsInvalidFormatValue(String badValue) {
        assertThrows(IllegalArgumentException.class,
                () -> CliArguments.parse(new String[]{"--input", "file.txt", "--format", badValue}));
    }

    @Test void propagatesNonNumericPortValue() {
        assertThrows(NumberFormatException.class,
                () -> CliArguments.parse(new String[]{"--input", "file.txt", "--src-port", "abc"}));
    }

    @Test void propagatesMalformedArgumentError() {
        assertThrows(IllegalArgumentException.class,
                () -> CliArguments.parse(new String[]{"--input", "file.txt", "unexpected"}));
    }

    @Test void ignoresUnrecognizedFlags() {
        Optional<CliArguments> parsed = CliArguments.parse(new String[]{"--input", "file.txt", "--unknown-flag", "value"});
        assertTrue(parsed.isPresent());
        assertEquals("file.txt", parsed.get().input());
    }
}

