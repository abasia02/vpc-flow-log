package com.interview.flowlog.model;

import java.util.Objects;
import java.util.regex.Pattern;

/** A single parsed flow-log entry, normalized to the fields common to every supported format. */
public final class FlowLogRecord {
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");

    private final int version;
    private final String accountId;
    private final String interfaceId;
    private final String sourceIp;
    private final String destinationIp;
    private final int sourcePort;
    private final int destinationPort;
    private final int protocol;
    private final long packets;
    private final long bytes;
    private final long start;
    private final long end;
    private final String action;
    private final String logStatus;
    private final String originalLine;

    public FlowLogRecord(int version, String accountId, String interfaceId,
                          String sourceIp, String destinationIp,
                          int sourcePort, int destinationPort, int protocol,
                          long packets, long bytes, long start, long end,
                          String action, String logStatus, String originalLine) {
        requireIpv4(sourceIp, "sourceIp");
        requireIpv4(destinationIp, "destinationIp");
        this.version = version;
        this.accountId = accountId;
        this.interfaceId = interfaceId;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
        this.packets = packets;
        this.bytes = bytes;
        this.start = start;
        this.end = end;
        this.action = action;
        this.logStatus = logStatus;
        this.originalLine = originalLine;
    }

    private static void requireIpv4(String ip, String fieldName) {
        if (ip == null || !IPV4_PATTERN.matcher(ip).matches()) {
            throw new IllegalArgumentException("Not a valid IPv4 address for " + fieldName + ": " + ip);
        }
    }

    public int version() {
        return version;
    }

    public String accountId() {
        return accountId;
    }

    public String interfaceId() {
        return interfaceId;
    }

    public String sourceIp() {
        return sourceIp;
    }

    public String destinationIp() {
        return destinationIp;
    }

    public int sourcePort() {
        return sourcePort;
    }

    public int destinationPort() {
        return destinationPort;
    }

    public int protocol() {
        return protocol;
    }

    public long packets() {
        return packets;
    }

    public long bytes() {
        return bytes;
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    public String action() {
        return action;
    }

    public String logStatus() {
        return logStatus;
    }

    public String originalLine() {
        return originalLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlowLogRecord that)) {
            return false;
        }
        return version == that.version && sourcePort == that.sourcePort && destinationPort == that.destinationPort
                && protocol == that.protocol && packets == that.packets && bytes == that.bytes
                && start == that.start && end == that.end
                && Objects.equals(accountId, that.accountId) && Objects.equals(interfaceId, that.interfaceId)
                && Objects.equals(sourceIp, that.sourceIp) && Objects.equals(destinationIp, that.destinationIp)
                && Objects.equals(action, that.action) && Objects.equals(logStatus, that.logStatus)
                && Objects.equals(originalLine, that.originalLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, accountId, interfaceId, sourceIp, destinationIp, sourcePort, destinationPort,
                protocol, packets, bytes, start, end, action, logStatus, originalLine);
    }

    @Override
    public String toString() {
        return "FlowLogRecord[version=" + version + ", accountId=" + accountId + ", interfaceId=" + interfaceId
                + ", sourceIp=" + sourceIp + ", destinationIp=" + destinationIp + ", sourcePort=" + sourcePort
                + ", destinationPort=" + destinationPort + ", protocol=" + protocol + ", packets=" + packets
                + ", bytes=" + bytes + ", start=" + start + ", end=" + end + ", action=" + action
                + ", logStatus=" + logStatus + ", originalLine=" + originalLine + "]";
    }
}

