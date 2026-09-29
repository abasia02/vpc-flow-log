package com.interview.flowlog.processor;

import com.interview.flowlog.model.ConnectionKey;
import com.interview.flowlog.model.FilterCriteria;
import com.interview.flowlog.model.ProcessedResult;
import com.interview.flowlog.parser.impl.DefaultV2FlowLogParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class FlowLogProcessorTest {
    @TempDir Path tempDir;
    private final FlowLogProcessor processor = new FlowLogProcessor(new DefaultV2FlowLogParser());

    @ParameterizedTest
    @MethodSource("filterScenarios")
    void filtersMatchExpectedCount(FilterCriteria criteria, int expectedMatches) throws Exception {
        assertEquals(expectedMatches, processor.process(writeSample(), criteria).matchedLines().size());
    }

    static Stream<Arguments> filterScenarios() {
        return Stream.of(
                Arguments.of(new FilterCriteria(Optional.of("10.0.0.1"), Optional.of("10.0.0.2"), Optional.empty(), Optional.empty()), 2),
                Arguments.of(new FilterCriteria(Optional.empty(), Optional.empty(), Optional.of(49153), Optional.of(443)), 2),
                Arguments.of(new FilterCriteria(Optional.of("10.0.0.3"), Optional.empty(), Optional.empty(), Optional.empty()), 1));
    }

    @Test void countsRepeatedFiveTuple() throws Exception {
        ProcessedResult r = processor.process(writeSample(), FilterCriteria.empty());
        ConnectionKey key = new ConnectionKey("10.0.0.1", 49153, "10.0.0.2", 443, 6);
        assertEquals(2L, r.connectionCounts().get(key));
    }

    @Test void skipsAndCountsMalformedRows() throws Exception {
        Path file = tempDir.resolve("bad.log");
        Files.writeString(file, "bad row\n2 123 eni-x 10.0.0.1 10.0.0.2 1 2 6 1 2 3 4 ACCEPT OK\n");
        ProcessedResult r = processor.process(file, FilterCriteria.empty());
        assertEquals(2, r.totalLines()); assertEquals(1, r.malformedLines());
    }

    @Test void throwsIOExceptionForMissingFile() {
        Path missing = tempDir.resolve("does-not-exist.log");
        assertThrows(IOException.class, () -> processor.process(missing, FilterCriteria.empty()));
    }

    private Path writeSample() throws Exception {
        Path file = tempDir.resolve("flow.log");
        Files.writeString(file,
            "2 123456789010 eni-a 10.0.0.1 10.0.0.2 49153 443 6 10 840 1620140661 1620140721 ACCEPT OK\n" +
            "2 123456789010 eni-a 10.0.0.1 10.0.0.2 49153 443 6 5 420 1620140800 1620140860 ACCEPT OK\n" +
            "2 123456789010 eni-b 10.0.0.3 10.0.0.4 53000 53 17 2 150 1620140900 1620140960 ACCEPT OK\n");
        return file;
    }
}

