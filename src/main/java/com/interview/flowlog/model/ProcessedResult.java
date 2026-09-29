package com.interview.flowlog.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** The outcome of processing a flow-log file: filtered rows, aggregate 5-tuple connection counts, and line totals. */
public final class ProcessedResult {
    private final List<String> matchedLines;
    private final Map<ConnectionKey, Long> connectionCounts;
    private final long totalLines;
    private final long malformedLines;

    public ProcessedResult(List<String> matchedLines, Map<ConnectionKey, Long> connectionCounts,
                            long totalLines, long malformedLines) {
        this.matchedLines = List.copyOf(matchedLines);
        this.connectionCounts = Map.copyOf(connectionCounts);
        this.totalLines = totalLines;
        this.malformedLines = malformedLines;
    }

    public List<String> matchedLines() {
        return matchedLines;
    }

    public Map<ConnectionKey, Long> connectionCounts() {
        return connectionCounts;
    }

    public long totalLines() {
        return totalLines;
    }

    public long malformedLines() {
        return malformedLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProcessedResult that)) {
            return false;
        }
        return totalLines == that.totalLines && malformedLines == that.malformedLines
                && Objects.equals(matchedLines, that.matchedLines)
                && Objects.equals(connectionCounts, that.connectionCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchedLines, connectionCounts, totalLines, malformedLines);
    }

    @Override
    public String toString() {
        return "ProcessedResult[matchedLines=" + matchedLines + ", connectionCounts=" + connectionCounts
                + ", totalLines=" + totalLines + ", malformedLines=" + malformedLines + "]";
    }
}

