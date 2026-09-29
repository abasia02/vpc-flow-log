package com.interview.flowlog.cli;

import com.interview.flowlog.model.FilterCriteria;
import com.interview.flowlog.parser.FlowLogFormat;
import com.interview.flowlog.util.CliOptions;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Parses raw CLI args into typed options for {@link FlowLogCli}. */
public final class CliArguments {
    private static final String SHOW_COUNTS = "show-counts";
    private static final Set<String> BOOLEAN_FLAGS = Set.of(SHOW_COUNTS);

    private final String input;
    private final FlowLogFormat format;
    private final FilterCriteria criteria;
    private final boolean showCounts;

    public CliArguments(String input, FlowLogFormat format, FilterCriteria criteria, boolean showCounts) {
        this.input = input;
        this.format = format;
        this.criteria = criteria;
        this.showCounts = showCounts;
    }

    /** Returns empty (after printing usage) when the required {@code --input} option is missing. */
    public static Optional<CliArguments> parse(String[] args) {
        CliOptions options = CliOptions.parse(args, BOOLEAN_FLAGS);
        if (!options.has("input")) {
            printUsage();
            return Optional.empty();
        }

        FilterCriteria criteria = new FilterCriteria(
                options.get("src-ip"), options.get("dst-ip"), options.getInt("src-port"), options.getInt("dst-port"));
        FlowLogFormat format = options.get("format").map(FlowLogFormat::fromCliValue).orElse(FlowLogFormat.DEFAULT_V2);

        return Optional.of(new CliArguments(options.get("input").orElseThrow(), format, criteria, options.has(SHOW_COUNTS)));
    }

    public String input() {
        return input;
    }

    public FlowLogFormat format() {
        return format;
    }

    public FilterCriteria criteria() {
        return criteria;
    }

    public boolean showCounts() {
        return showCounts;
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar target/vpc-flow-log-analyzer-1.0.0.jar --input <file> " +
                "[--format v2|v3] [--src-ip <IPv4>] [--dst-ip <IPv4>] [--src-port <port>] [--dst-port <port>] [--show-counts]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CliArguments that)) {
            return false;
        }
        return showCounts == that.showCounts && Objects.equals(input, that.input)
                && format == that.format && Objects.equals(criteria, that.criteria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, format, criteria, showCounts);
    }

    @Override
    public String toString() {
        return "CliArguments[input=" + input + ", format=" + format + ", criteria=" + criteria
                + ", showCounts=" + showCounts + "]";
    }
}

