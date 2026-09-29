package com.interview.flowlog.model;

import java.util.Objects;

/** A connection is uniquely represented by the requested network 5-tuple. */
public final class ConnectionKey {
    private final String sourceIp;
    private final int sourcePort;
    private final String destinationIp;
    private final int destinationPort;
    private final int protocol;

    public ConnectionKey(String sourceIp, int sourcePort, String destinationIp, int destinationPort, int protocol) {
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
    }

    public static ConnectionKey from(FlowLogRecord r) {
        return new ConnectionKey(r.sourceIp(), r.sourcePort(), r.destinationIp(), r.destinationPort(), r.protocol());
    }

    public String sourceIp() {
        return sourceIp;
    }

    public int sourcePort() {
        return sourcePort;
    }

    public String destinationIp() {
        return destinationIp;
    }

    public int destinationPort() {
        return destinationPort;
    }

    public int protocol() {
        return protocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConnectionKey that)) {
            return false;
        }
        return sourcePort == that.sourcePort && destinationPort == that.destinationPort && protocol == that.protocol
                && Objects.equals(sourceIp, that.sourceIp) && Objects.equals(destinationIp, that.destinationIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIp, sourcePort, destinationIp, destinationPort, protocol);
    }

    @Override
    public String toString() {
        return "ConnectionKey[sourceIp=" + sourceIp + ", sourcePort=" + sourcePort
                + ", destinationIp=" + destinationIp + ", destinationPort=" + destinationPort
                + ", protocol=" + protocol + "]";
    }
}

