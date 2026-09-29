package com.interview.flowlog.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FlowLogCliTest {
    @TempDir Path tempDir;
    private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    private final ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach void redirectStreams() {
        System.setOut(new PrintStream(capturedOut));
        System.setErr(new PrintStream(capturedErr));
    }

    @AfterEach void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test void returnsZeroAndPrintsResultsOnSuccess() throws Exception {
        Path file = tempDir.resolve("flow.log");
        Files.writeString(file,
                "2 123456789010 eni-a 10.0.0.1 10.0.0.2 49153 443 6 10 840 1620140661 1620140721 ACCEPT OK\n");

        int exitCode = FlowLogCli.run(new String[]{"--input", file.toString(), "--show-counts"});

        assertEquals(0, exitCode);
        assertTrue(capturedOut.toString().contains("Matched flow-log rows"));
        assertTrue(capturedOut.toString().contains("Connection counts"));
    }

    @Test void returnsOneAndPrintsUsageWhenInputMissing() {
        int exitCode = FlowLogCli.run(new String[]{});

        assertEquals(1, exitCode);
        assertTrue(capturedOut.toString().contains("Usage:"));
    }

    @ParameterizedTest
    @MethodSource("errorScenarios")
    void returnsTwoWithCleanErrorMessage(String[] args, String expectedErrorSubstring) {
        int exitCode = FlowLogCli.run(args);

        assertEquals(2, exitCode);
        assertTrue(capturedErr.toString().contains(expectedErrorSubstring));
    }

    static Stream<Arguments> errorScenarios() {
        return Stream.of(
                Arguments.of(new String[]{"--input", "does-not-exist.txt"}, "Error:"),
                Arguments.of(new String[]{"--input", "any.txt", "--format", "bogus"}, "Unknown --format value"));
    }
}

