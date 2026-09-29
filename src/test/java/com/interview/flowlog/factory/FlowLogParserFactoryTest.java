package com.interview.flowlog.factory;

import com.interview.flowlog.parser.FlowLogFormat;
import com.interview.flowlog.parser.impl.DefaultV2FlowLogParser;
import com.interview.flowlog.parser.impl.TcpFlagsV3FlowLogParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlowLogParserFactoryTest {
    @Test void returnsDefaultV2Parser() {
        assertInstanceOf(DefaultV2FlowLogParser.class, FlowLogParserFactory.forFormat(FlowLogFormat.DEFAULT_V2));
    }

    @Test void returnsTcpFlagsV3Parser() {
        assertInstanceOf(TcpFlagsV3FlowLogParser.class, FlowLogParserFactory.forFormat(FlowLogFormat.V3_TCP_FLAGS));
    }
}
