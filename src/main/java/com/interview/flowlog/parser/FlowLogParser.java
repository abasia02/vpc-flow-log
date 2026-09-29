package com.interview.flowlog.parser;

import com.interview.flowlog.model.FlowLogRecord;

/** Parses one flow-log text line into a {@link FlowLogRecord}. Each format has its own implementation. */
public interface FlowLogParser {
    FlowLogRecord parse(String line);
}
