#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/enhanced/service-suite/src/main/java"
OUT=$(mktemp -d)
javac -Xlint:all -d "$OUT" $(find "$SRC" -type f -name '*.java' | sort) "$EVIDENCE_DIR/AlgorithmsVerification.java"
java -cp "$OUT" edu.snhu.cs499.verification.AlgorithmsVerification
