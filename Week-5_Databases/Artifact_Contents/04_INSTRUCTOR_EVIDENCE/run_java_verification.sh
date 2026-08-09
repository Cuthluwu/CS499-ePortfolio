#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/02_ENHANCED_DATABASE_ARTIFACT/Project2/app/src/main/java"
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
if command -v javac >/dev/null 2>&1; then
  COMPILER="javac"
else
  COMPILER="java --module jdk.compiler/com.sun.tools.javac.Main"
fi
$COMPILER -Xlint:all -d "$OUT" \
  "$SRC/com/example/project/security/PasswordHasher.java" \
  "$SRC/com/example/project/validation/InputValidator.java" \
  "$SRC/com/example/project/data/LegacyDataNormalizer.java" \
  "$EVIDENCE_DIR/StandaloneSecurityValidationTest.java"
java -cp "$OUT" StandaloneSecurityValidationTest
