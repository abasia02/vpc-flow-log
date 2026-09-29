package com.interview.flowlog.cli;

import com.interview.flowlog.model.ConnectionKey;
import com.interview.flowlog.model.ProcessedResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultPrinterTest {
    private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach void redirectStdOut() {
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach void restoreStdOut() {
        System.setOut(originalOut);
    }

    @Test void printsMatchedLinesAndSummary() {
        ProcessedResult result = new ProcessedResult(List.of("line one"), Map.of(), 1, 0);
        ResultPrinter.print(result, false);
        String printed = capturedOut.toString();
        assertTrue(printed.contains("line one"));
        assertTrue(printed.contains("Summary: total=1, matched=1, malformed=0"));
        assertFalse(printed.contains("Connection counts"));
    }

    @Test void printsConnectionCountsWhenShowCountsTrue() {
        ConnectionKey key = new ConnectionKey("10.0.0.1", 1, "10.0.0.2", 2, 6);
        ProcessedResult result = new ProcessedResult(List.of(), Map.of(key, 3L), 1, 0);
        ResultPrinter.print(result, true);
        String printed = capturedOut.toString();
        assertTrue(printed.contains("Connection counts"));
        assertTrue(printed.contains(key + " = 3"));
    }

    @Test void sortsConnectionCountsDescendingByCount() {
        ConnectionKey low = new ConnectionKey("10.0.0.1", 1, "10.0.0.2", 2, 6);
        ConnectionKey high = new ConnectionKey("10.0.0.3", 3, "10.0.0.4", 4, 17);
        ProcessedResult result = new ProcessedResult(List.of(), Map.of(low, 1L, high, 5L), 2, 0);

        ResultPrinter.print(result, true);

        String printed = capturedOut.toString();
        int highIndex = printed.indexOf(high + " = 5");
        int lowIndex = printed.indexOf(low + " = 1");
        assertTrue(highIndex >= 0);
        assertTrue(lowIndex >= 0);
        assertTrue(highIndex < lowIndex);
    }
}
