# VPC Flow Log Analyzer

Java command-line application that parses AWS VPC Flow Log records, filters records, and counts network connections by 5-tuple.

## Features

- Parse plain-text AWS VPC Flow Logs in the default version-2 format, or the custom version-3 "TCP flag sequence" format, selected via `--format`.
- Filter by source IPv4 address and/or destination IPv4 address.
- Extension: filter by source port and/or destination port.
- Extension: count occurrences of each connection using `(source IP, source port, destination IP, destination port, protocol)`.
- Stream the input line-by-line instead of loading the complete file into memory.
- Skip malformed records and report their count.
- Unit tests for parsing, filtering, connection counting, malformed input, format selection, CLI argument parsing, and output formatting — including parametrized tests for repeated-shape scenarios.

## Supported Formats

| `--format` | Format | Fields | AWS reference |
|---|---|---|---|
| `v2` (default) | Default version-2 | 14 | [Flow log records](https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html) |
| `v3` | Custom version-3 "TCP flag sequence" | 21 | [TCP flag sequence example](https://docs.aws.amazon.com/vpc/latest/userguide/flow-logs-records-examples.html#flow-log-example-tcp-flag) |

`version account-id interface-id srcaddr dstaddr srcport dstport protocol packets bytes start end action log-status` (v2)

`version vpc-id subnet-id instance-id interface-id account-id type srcaddr dstaddr srcport dstport pkt-srcaddr pkt-dstaddr protocol bytes packets start end action tcp-flags log-status` (v3)

Each format has its own `FlowLogParser` implementation; only the fields shared with v2 (5-tuple, packets, bytes, timestamps, action, log-status) are mapped onto `FlowLogRecord`. The v3-only fields (vpc-id, subnet-id, instance-id, type, pkt-addrs, tcp-flags) remain visible in the raw output line but are not separately modeled.

Both sample-data files mix well-formed records (including ICMP and a repeated 5-tuple to demonstrate connection counting) with deliberately malformed lines — wrong field count, a non-numeric field, a non-IPv4 address (IPv6, since the assignment requires IPv4 only — see Assumptions), unparseable junk, and (in `flow-logs.txt`) an AWS `NODATA` record, which is valid AWS output but not parseable by this simplified tool since its `-` placeholders can't be parsed as numbers — plus a blank line, which is silently skipped rather than counted as malformed. Running either file with `--show-counts` reports the resulting `total`/`matched`/`malformed` breakdown.

## Assumptions

The assignment specifies IPv4 only, and `FlowLogRecord` enforces this: its constructor validates `sourceIp`/`destinationIp` against a strict IPv4 pattern (each octet 0-255, no leading zeros) and rejects anything else — including IPv6 — as malformed. Filters use exact IPv4 string matching (no CIDR, no normalization of equivalent representations). The program assumes valid records for the selected format; malformed records are skipped and counted.

## Project Structure

```text
vpc-flow-log-analyzer/
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat          # generated Gradle Wrapper scripts
├── gradle/wrapper/                # generated Gradle Wrapper files
├── README.md
├── sample-data/
│   ├── flow-logs.txt
│   ├── flow-logs-tcp-flags-v3.txt
│   └── generate-large-sample.py    # regenerates flow-logs-20mb.txt (gitignored, not checked in)
└── src/
    ├── main/java/com/interview/flowlog/
    │   ├── cli/FlowLogCli.java                       # main() delegates to run(); run() returns an exit code
    │   ├── cli/CliArguments.java                      # assembles typed options from CliOptions
    │   ├── cli/ResultPrinter.java                      # formats/prints a ProcessedResult
    │   ├── util/CliOptions.java                        # generic --key value tokenizer + typed getters
    │   ├── model/{FlowLogRecord,ConnectionKey,FilterCriteria,ProcessedResult}.java
    │   ├── parser/FlowLogParser.java                 # interface
    │   ├── parser/FlowLogFormat.java                 # enum: DEFAULT_V2, V3_TCP_FLAGS; fromCliValue()
    │   ├── parser/impl/DefaultV2FlowLogParser.java
    │   ├── parser/impl/TcpFlagsV3FlowLogParser.java
    │   ├── factory/FlowLogParserFactory.java          # picks the parser for a format
    │   └── processor/FlowLogProcessor.java            # takes a FlowLogParser via constructor injection
    └── test/java/com/interview/flowlog/
        ├── model/FlowLogRecordTest.java              # IPv4 validation
        ├── cli/{FlowLogCliTest,CliArgumentsTest,ResultPrinterTest}.java
        ├── util/CliOptionsTest.java
        ├── parser/impl/{DefaultV2FlowLogParserTest,TcpFlagsV3FlowLogParserTest}.java
        ├── factory/FlowLogParserFactoryTest.java
        └── processor/FlowLogProcessorTest.java
```

## Requirements

- Java 17+
- Gradle 9.x (only needed once to generate the wrapper; after that use `./gradlew`)

## Build and Test

If the Gradle Wrapper has not been generated yet, run this once on a machine with Gradle installed:

```bash
gradle wrapper --gradle-version 9.3.0
```

Commit `gradlew`, `gradlew.bat`, and `gradle/wrapper/` to GitHub. Then everyone can build without installing Gradle.

**Build/compile:**

```bash
./gradlew build          # compile + test + package jar
./gradlew compileJava    # compile only (no tests)
./gradlew clean build    # clean rebuild
```

**Run tests only:**

```bash
./gradlew test
./gradlew test --info     # verbose output
```

## Usage

```bash
# Option 1: via the built jar
java -jar build/libs/vpc-flow-log-analyzer-1.0.0.jar \
  --input sample-data/flow-logs.txt \
  --src-ip 10.0.0.1 \
  --dst-ip 10.0.0.2 \
  --src-port 49153 \
  --dst-port 443 \
  --show-counts

# Option 2: via Gradle's application plugin (no need to build jar first)
./gradlew run --args="--input sample-data/flow-logs.txt --show-counts"
```

All filters are optional. `--input` is required. `--format` defaults to `v2`.

Examples:

```bash
# Source IP only
java -jar build/libs/vpc-flow-log-analyzer-1.0.0.jar --input sample-data/flow-logs.txt --src-ip 10.0.0.1

# Destination IP + destination port
java -jar build/libs/vpc-flow-log-analyzer-1.0.0.jar --input sample-data/flow-logs.txt --dst-ip 10.0.0.2 --dst-port 443

# Show every row and connection counts
java -jar build/libs/vpc-flow-log-analyzer-1.0.0.jar --input sample-data/flow-logs.txt --show-counts

# Custom v3 TCP-flag-sequence format
java -jar build/libs/vpc-flow-log-analyzer-1.0.0.jar --input sample-data/flow-logs-tcp-flags-v3.txt --format v3 --show-counts
```

## Design

`FlowLogParser` is an interface with one responsibility: convert a line into a typed `FlowLogRecord`. `DefaultV2FlowLogParser` and `TcpFlagsV3FlowLogParser` are its two implementations, one per supported format. `FlowLogParserFactory` (factory pattern) holds a static `Map<FlowLogFormat, FlowLogParser>` built once at class-load — each format maps to a shared parser instance (both implementations are stateless) — and `forFormat()` just looks it up; adding a future format means adding a new implementation + one new map entry, with no changes to filtering, counting, or CLI output. `FlowLogProcessor` only takes a `FlowLogParser` via constructor injection — it has no knowledge of `FlowLogFormat` or the factory; `FlowLogCli` resolves the parser via `FlowLogParserFactory.forFormat(...)` itself and passes it in. `FilterCriteria` encapsulates optional AND-based filters. `FlowLogProcessor` handles streaming I/O and aggregates 5-tuple counts.

`FlowLogCli` is a thin entry point only. `main()` just calls `System.exit(run(args))` — `run()` holds the actual logic and returns an exit code instead of calling `System.exit()` itself, which is what makes it directly unit-testable. `CliOptions` is a fully generic `--key value` tokenizer with typed accessors (`has`/`get`/`getInt`) — no flow-log-specific knowledge, reusable for any future CLI flags. `CliArguments` builds on it: assembles the typed options (input path, format, filter criteria, show-counts) and owns the usage text; format-string parsing itself lives on `FlowLogFormat.fromCliValue()`, since the enum should own its own string representation. `ResultPrinter` formats and prints a `ProcessedResult`. `run()` just wires `CliArguments` → `FlowLogProcessor` → `ResultPrinter`.

All data types (`FlowLogRecord`, `ConnectionKey`, `FilterCriteria`, `ProcessedResult`, `CliOptions`, `CliArguments`) are plain immutable classes: private final fields, a constructor, accessor methods, and manually implemented `equals()`/`hashCode()`/`toString()`. `ConnectionKey` in particular relies on its own `equals()`/`hashCode()` to work correctly as a `HashMap` key. `FlowLogRecord`'s constructor validates `sourceIp`/`destinationIp` against a strict IPv4 regex and throws `IllegalArgumentException` otherwise — since both parsers funnel through this one constructor, IPv4 enforcement is written once and covers every current and future format automatically, and it reuses the existing malformed-record handling in `FlowLogProcessor` for free (no processor changes needed).

### Complexity

For `n` log records and `k` distinct connections:

- Parsing/filtering: **O(n)** time.
- Connection counting: **O(n)** expected time using a hash map.
- Connection-count storage: **O(k)**.
- Input-file storage: **O(1)** with respect to file size because records are streamed.
- Matched rows: **O(m)** where `m` is the number of matching rows retained for output.

For a maximum 20 MB input file this is intentionally simple and efficient without introducing unnecessary parallelism. Verified against a generated 20 MiB / ~202K-line sample (regenerate with `python3 sample-data/generate-large-sample.py`; the output itself is gitignored, not checked in, since it's fully reproducible): processes in well under a second even with the JVM heap capped at `-Xmx64m`, confirming the file is genuinely streamed rather than buffered in memory.

## Error Handling

Malformed records are skipped so a single bad line does not abort processing. The final summary reports how many malformed non-blank lines were encountered. Expected file/CLI-level errors (bad arguments, unknown `--format`, missing/unreadable input file) print a single-line `Error: ...` message and exit non-zero. Any other, unexpected exception prints a full stack trace instead of a generic message, so a real bug stays diagnosable rather than silently swallowed.

## Potential Production Enhancements

For a larger production system, reasonable extensions include CIDR filters, IPv6 support as a parallel address family (currently IPv6 is explicitly rejected as malformed, not accepted), additional custom AWS flow-log formats (v4/v5 fields, NAT/transit-gateway formats), protocol-name mapping, streaming matched output directly to a file/output stream, structured logging, metrics, and configurable malformed-record policy.
