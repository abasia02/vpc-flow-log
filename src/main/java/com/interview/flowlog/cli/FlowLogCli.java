package com.interview.flowlog.cli;

import com.interview.flowlog.factory.FlowLogParserFactory;
import com.interview.flowlog.model.ProcessedResult;
import com.interview.flowlog.parser.FlowLogParser;
import com.interview.flowlog.processor.FlowLogProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/** Command-line entry point: parses args, runs the processor, and prints results. */
public final class FlowLogCli {
    private FlowLogCli() {
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }

    /** Runs the CLI end-to-end. Returns 0 on success, 1 if required arguments are missing, 2 on any error. */
    static int run(String[] args) {
        try {
            Optional<CliArguments> cliArgs = CliArguments.parse(args);
            if (cliArgs.isEmpty()) {
                return 1;
            }

            CliArguments arguments = cliArgs.get();
            FlowLogParser parser = FlowLogParserFactory.forFormat(arguments.format());
            ProcessedResult result = new FlowLogProcessor(parser).process(Path.of(arguments.input()), arguments.criteria());

            ResultPrinter.print(result, arguments.showCounts());
            return 0;
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            return 2;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
}
