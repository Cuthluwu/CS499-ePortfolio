#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/03_WEEK4_ALGORITHMS_ENHANCED/src/main/java"
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
if command -v javac >/dev/null 2>&1; then
  javac -Xlint:all -d "$OUT" $(find "$SRC" -type f -name '*.java' | sort) "$EVIDENCE_DIR/AlgorithmsVerification.java"
else
  java --module jdk.compiler/com.sun.tools.javac.Main -Xlint:all -d "$OUT" $(find "$SRC" -type f -name '*.java' | sort) "$EVIDENCE_DIR/AlgorithmsVerification.java"
fi
java -cp "$OUT" edu.snhu.cs499.verification.AlgorithmsVerification
