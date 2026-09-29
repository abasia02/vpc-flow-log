package com.interview.flowlog.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** A generic {@code --key value} option map parsed from raw CLI args, with named boolean flags. */
public final class CliOptions {
    private final Map<String, String> values;

    public CliOptions(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static CliOptions parse(String[] args, Set<String> booleanFlags) {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + arg);
            }
            String key = arg.substring(2);
            if (booleanFlags.contains(key)) {
                result.put(key, "true");
            } else {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for " + arg);
                }
                result.put(key, args[++i]);
            }
        }
        return new CliOptions(result);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public Optional<Integer> getInt(String key) {
        return get(key).map(Integer::parseInt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CliOptions that)) {
            return false;
        }
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "CliOptions[values=" + values + "]";
    }
}

