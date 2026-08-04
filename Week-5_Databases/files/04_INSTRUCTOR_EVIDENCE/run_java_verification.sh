#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/enhanced/Project2/app/src/main/java"
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
javac -Xlint:all -d "$OUT" \
  "$SRC/com/example/project/security/PasswordHasher.java" \
  "$SRC/com/example/project/validation/InputValidator.java" \
  "$SRC/com/example/project/data/LegacyDataNormalizer.java" \
  "$EVIDENCE_DIR/StandaloneSecurityValidationTest.java"
java -cp "$OUT" StandaloneSecurityValidationTest
