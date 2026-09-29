package com.interview.flowlog.parser;

/** Supported AWS VPC Flow Log record formats. */
public enum FlowLogFormat {
    /** Default version 2 format: 14 space-separated fields. */
    DEFAULT_V2,
    /** Custom version 3 "TCP flag sequence" format: 21 space-separated fields. */
    V3_TCP_FLAGS;

    /** Maps a CLI {@code --format} value ("v2"/"v3") to a format. */
    public static FlowLogFormat fromCliValue(String value) {
        return switch (value) {
            case "v2" -> DEFAULT_V2;
            case "v3" -> V3_TCP_FLAGS;
            default -> throw new IllegalArgumentException("Unknown --format value: " + value);
        };
    }
}
