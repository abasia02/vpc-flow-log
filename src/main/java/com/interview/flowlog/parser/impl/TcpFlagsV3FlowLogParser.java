package com.interview.flowlog.parser.impl;

import com.interview.flowlog.model.FlowLogRecord;
import com.interview.flowlog.parser.FlowLogParser;

/**
 * Parses the 21-field custom version-3 "TCP flag sequence" format from the AWS flow log examples:
 * version vpc-id subnet-id instance-id interface-id account-id type srcaddr dstaddr srcport dstport
 * pkt-srcaddr pkt-dstaddr protocol bytes packets start end action tcp-flags log-status
 *
 * Only the fields also present in the default v2 format are kept on {@link FlowLogRecord};
 * vpc-id/subnet-id/instance-id/type/pkt-addrs/tcp-flags stay in the raw {@code originalLine}.
 */
public final class TcpFlagsV3FlowLogParser implements FlowLogParser {
    private static final int EXPECTED_FIELDS = 21;

    @Override
    public FlowLogRecord parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Flow log line is blank");
        }
        String[] f = line.trim().split("\\s+");
        if (f.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException("Expected 21 fields but found " + f.length + ": " + line);
        }
        try {
            return new FlowLogRecord(
                    Integer.parseInt(f[0]), f[5], f[4], f[7], f[8],
                    Integer.parseInt(f[9]), Integer.parseInt(f[10]), Integer.parseInt(f[13]),
                    Long.parseLong(f[15]), Long.parseLong(f[14]), Long.parseLong(f[16]), Long.parseLong(f[17]),
                    f[18], f[20], line);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric field: " + line, e);
        }
    }
}
