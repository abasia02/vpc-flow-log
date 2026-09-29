package com.interview.flowlog.model;

import java.util.Objects;
import java.util.Optional;

/** Optional, AND-combined filters over a {@link FlowLogRecord}'s source/destination IP and port. An absent filter always matches. */
public final class FilterCriteria {
    private final Optional<String> sourceIp;
    private final Optional<String> destinationIp;
    private final Optional<Integer> sourcePort;
    private final Optional<Integer> destinationPort;

    public FilterCriteria(Optional<String> sourceIp, Optional<String> destinationIp,
                           Optional<Integer> sourcePort, Optional<Integer> destinationPort) {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public static FilterCriteria empty() {
        return new FilterCriteria(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public Optional<String> sourceIp() {
        return sourceIp;
    }

    public Optional<String> destinationIp() {
        return destinationIp;
    }

    public Optional<Integer> sourcePort() {
        return sourcePort;
    }

    public Optional<Integer> destinationPort() {
        return destinationPort;
    }

    public boolean matches(FlowLogRecord r) {
        return sourceIp.map(v -> v.equals(r.sourceIp())).orElse(true)
                && destinationIp.map(v -> v.equals(r.destinationIp())).orElse(true)
                && sourcePort.map(v -> v == r.sourcePort()).orElse(true)
                && destinationPort.map(v -> v == r.destinationPort()).orElse(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterCriteria that)) {
            return false;
        }
        return Objects.equals(sourceIp, that.sourceIp) && Objects.equals(destinationIp, that.destinationIp)
                && Objects.equals(sourcePort, that.sourcePort) && Objects.equals(destinationPort, that.destinationPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIp, destinationIp, sourcePort, destinationPort);
    }

    @Override
    public String toString() {
        return "FilterCriteria[sourceIp=" + sourceIp + ", destinationIp=" + destinationIp
                + ", sourcePort=" + sourcePort + ", destinationPort=" + destinationPort + "]";
    }
}

