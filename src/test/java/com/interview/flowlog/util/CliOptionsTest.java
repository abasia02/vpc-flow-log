package com.interview.flowlog.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CliOptionsTest {
    @Test void parsesKeyValuePairs() {
        CliOptions options = CliOptions.parse(new String[]{"--input", "file.txt", "--src-ip", "10.0.0.1"}, Set.of());
        assertEquals(Optional.of("file.txt"), options.get("input"));
        assertEquals(Optional.of("10.0.0.1"), options.get("src-ip"));
    }

    @Test void treatsNamedFlagsAsBooleanTrueWithoutConsumingNextArg() {
        CliOptions options = CliOptions.parse(new String[]{"--show-counts", "--input", "file.txt"}, Set.of("show-counts"));
        assertTrue(options.has("show-counts"));
        assertEquals(Optional.of("true"), options.get("show-counts"));
        assertEquals(Optional.of("file.txt"), options.get("input"));
    }

    @Test void rejectsArgumentNotStartingWithDashDash() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"input"}, Set.of()));
    }

    @Test void rejectsMissingValueForNonFlagOption() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--input"}, Set.of()));
    }

    @Test void hasReturnsFalseAndGetReturnsEmptyForAbsentKey() {
        CliOptions options = CliOptions.parse(new String[]{"--input", "file.txt"}, Set.of());
        assertFalse(options.has("format"));
        assertEquals(Optional.empty(), options.get("format"));
    }

    @Test void getIntParsesNumericValue() {
        CliOptions options = CliOptions.parse(new String[]{"--src-port", "49153"}, Set.of());
        assertEquals(Optional.of(49153), options.getInt("src-port"));
    }

    @Test void getIntThrowsForNonNumericValue() {
        CliOptions options = CliOptions.parse(new String[]{"--src-port", "abc"}, Set.of());
        assertThrows(NumberFormatException.class, () -> options.getInt("src-port"));
    }
}
