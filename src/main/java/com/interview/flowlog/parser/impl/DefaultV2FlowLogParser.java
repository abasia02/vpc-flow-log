package com.interview.flowlog.parser.impl;

import com.interview.flowlog.model.FlowLogRecord;
import com.interview.flowlog.parser.FlowLogParser;

/** Parses the 14-field AWS VPC Flow Logs default version-2 text format. */
public final class DefaultV2FlowLogParser implements FlowLogParser {
    private static final int EXPECTED_FIELDS = 14;

    @Override
    public FlowLogRecord parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Flow log line is blank");
        }
        String[] f = line.trim().split("\\s+");
        if (f.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException("Expected 14 fields but found " + f.length + ": " + line);
        }
        try {
            return new FlowLogRecord(
                    Integer.parseInt(f[0]), f[1], f[2], f[3], f[4],
                    Integer.parseInt(f[5]), Integer.parseInt(f[6]), Integer.parseInt(f[7]),
                    Long.parseLong(f[8]), Long.parseLong(f[9]), Long.parseLong(f[10]), Long.parseLong(f[11]),
                    f[12], f[13], line);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric field: " + line, e);
        }
    }
}
