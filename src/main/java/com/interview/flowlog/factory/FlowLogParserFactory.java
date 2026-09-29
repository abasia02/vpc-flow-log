package com.interview.flowlog.factory;

import com.interview.flowlog.parser.FlowLogFormat;
import com.interview.flowlog.parser.FlowLogParser;
import com.interview.flowlog.parser.impl.DefaultV2FlowLogParser;
import com.interview.flowlog.parser.impl.TcpFlagsV3FlowLogParser;

import java.util.Map;

/** Returns the {@link FlowLogParser} implementation for a given {@link FlowLogFormat}. */
public final class FlowLogParserFactory {
    private static final Map<FlowLogFormat, FlowLogParser> PARSERS = Map.of(
            FlowLogFormat.DEFAULT_V2, new DefaultV2FlowLogParser(),
            FlowLogFormat.V3_TCP_FLAGS, new TcpFlagsV3FlowLogParser());

    private FlowLogParserFactory() {
    }

    public static FlowLogParser forFormat(FlowLogFormat format) {
        FlowLogParser parser = PARSERS.get(format);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return parser;
    }
}
