#!/usr/bin/env python3
"""
Generates a ~20 MiB sample AWS VPC Flow Log (default version-2 format) to exercise
this project's streaming design target (see README "Complexity" section).

The output mixes well-formed records (drawn from a pool of repeating 5-tuples, so
connection counting has realistic repeats at scale) with a small, deterministic
sprinkling of malformed lines (wrong field count).

Usage:
    python3 sample-data/generate-large-sample.py

The generated file is NOT checked into version control (see .gitignore) since it
is fully reproducible from this script (fixed random seed).
"""
import random
from pathlib import Path

random.seed(42)

TARGET_BYTES = 20 * 1024 * 1024  # 20 MiB
OUT_PATH = Path(__file__).parent / "flow-logs-20mb.txt"

PROTOCOLS = [6, 17, 1]
ACTIONS = ["ACCEPT", "ACCEPT", "ACCEPT", "REJECT"]
POOL_SIZE = 500
MALFORMED_EVERY = 5000  # sprinkle a small, realistic fraction of malformed lines
START_TS = 1620140661


def build_connection_pool():
    pool = []
    for _ in range(POOL_SIZE):
        src = f"10.{random.randint(0, 255)}.{random.randint(0, 255)}.{random.randint(1, 254)}"
        dst = f"10.{random.randint(0, 255)}.{random.randint(0, 255)}.{random.randint(1, 254)}"
        sport = random.randint(1024, 65535)
        dport = random.choice([22, 80, 443, 53, 8080, 3389])
        proto = random.choice(PROTOCOLS)
        pool.append((src, dst, sport, dport, proto))
    return pool


def generate():
    pool = build_connection_pool()
    written = 0
    line_no = 0
    with open(OUT_PATH, "w") as f:
        while written < TARGET_BYTES:
            line_no += 1
            if line_no % MALFORMED_EVERY == 0:
                line = f"2 123456789010 eni-bad{line_no} 10.0.0.1 10.0.0.2 ACCEPT OK\n"
            else:
                src, dst, sport, dport, proto = random.choice(pool)
                packets = random.randint(1, 50)
                num_bytes = packets * random.randint(60, 1500)
                ts_start = START_TS + line_no
                ts_end = ts_start + random.randint(1, 60)
                action = random.choice(ACTIONS)
                line = (f"2 123456789010 eni-{line_no % 1000:04d} {src} {dst} {sport} {dport} "
                        f"{proto} {packets} {num_bytes} {ts_start} {ts_end} {action} OK\n")
            f.write(line)
            written += len(line)

    print(f"Wrote {line_no} lines, {written} bytes ({written / 1024 / 1024:.2f} MiB) to {OUT_PATH}")


if __name__ == "__main__":
    generate()
