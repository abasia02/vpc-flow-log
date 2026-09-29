package com.interview.flowlog.cli;

import com.interview.flowlog.model.ConnectionKey;
import com.interview.flowlog.model.ProcessedResult;

import java.util.Map;

/** Prints a {@link ProcessedResult} to the console. */
public final class ResultPrinter {
    private ResultPrinter() {
    }

    public static void print(ProcessedResult result, boolean showCounts) {
        System.out.println("Matched flow-log rows:");
        result.matchedLines().forEach(System.out::println);
        System.out.printf("%nSummary: total=%d, matched=%d, malformed=%d%n",
                result.totalLines(), result.matchedLines().size(), result.malformedLines());

        if (showCounts) {
            System.out.println("\nConnection counts (srcIP:srcPort -> dstIP:dstPort, protocol):");
            result.connectionCounts().entrySet().stream()
                    .sorted(Map.Entry.<ConnectionKey, Long>comparingByValue().reversed())
                    .forEach(e -> System.out.println(e.getKey() + " = " + e.getValue()));
        }
    }
}
