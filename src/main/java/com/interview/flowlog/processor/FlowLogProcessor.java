package com.interview.flowlog.processor;

import com.interview.flowlog.model.ConnectionKey;
import com.interview.flowlog.model.FilterCriteria;
import com.interview.flowlog.model.FlowLogRecord;
import com.interview.flowlog.model.ProcessedResult;
import com.interview.flowlog.parser.FlowLogParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Streams the input file line-by-line, avoiding loading a <=20 MB file into memory. */
public final class FlowLogProcessor {
    private final FlowLogParser parser;

    public FlowLogProcessor(FlowLogParser parser) {
        this.parser = parser;
    }

    /**
     * Streams and parses {@code input}, applying {@code criteria} to select which rows are returned.
     * Connection counts always reflect every successfully parsed record, regardless of the filter.
     */
    public ProcessedResult process(Path input, FilterCriteria criteria) throws IOException {
        List<String> matches = new ArrayList<>();
        Map<ConnectionKey, Long> counts = new HashMap<>();
        long total = 0, malformed = 0;

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.US_ASCII)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                total++;
                try {
                    FlowLogRecord record = parser.parse(line);
                    counts.merge(ConnectionKey.from(record), 1L, Long::sum);
                    if (criteria.matches(record)) {
                        matches.add(record.originalLine());
                    }
                } catch (IllegalArgumentException e) {
                    malformed++;
                }
            }
        }
        return new ProcessedResult(matches, counts, total, malformed);
    }
}
