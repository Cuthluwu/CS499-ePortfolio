#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/02_ENHANCED_SOFTWARE_DESIGN_ARTIFACT/cs320-service-suite/src/main/java"
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
if command -v javac >/dev/null 2>&1; then
  javac -Xlint:all -d "$OUT" $(find "$SRC" -type f -name '*.java' | sort) "$EVIDENCE_DIR/SoftwareDesignVerification.java"
else
  java --module jdk.compiler/com.sun.tools.javac.Main -Xlint:all -d "$OUT" $(find "$SRC" -type f -name '*.java' | sort) "$EVIDENCE_DIR/SoftwareDesignVerification.java"
fi
java -cp "$OUT" edu.snhu.cs499.verification.SoftwareDesignVerification
