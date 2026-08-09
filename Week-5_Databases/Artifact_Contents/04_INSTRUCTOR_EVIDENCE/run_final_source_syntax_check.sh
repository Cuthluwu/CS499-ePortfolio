#!/usr/bin/env sh
set -eu
EVIDENCE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$EVIDENCE_DIR/.." && pwd)
SRC="$ROOT/02_ENHANCED_DATABASE_ARTIFACT/Project2/app/src/main/java"
STUBS="$EVIDENCE_DIR/android_api_stubs"
OUT=$(mktemp -d)
trap 'find "$OUT" -type f -delete; find "$OUT" -depth -type d -empty -delete' EXIT

if command -v javac >/dev/null 2>&1; then
  COMPILER="javac"
else
  COMPILER="java --module jdk.compiler/com.sun.tools.javac.Main"
fi

$COMPILER -Xlint:all -d "$OUT" \
  "$STUBS/android/content/Context.java" \
  "$STUBS/android/content/ContentValues.java" \
  "$STUBS/android/database/Cursor.java" \
  "$STUBS/android/database/sqlite/SQLiteDatabase.java" \
  "$STUBS/android/database/sqlite/SQLiteOpenHelper.java" \
  "$STUBS/android/util/Log.java" \
  "$SRC/com/example/project/data/DatabaseContract.java" \
  "$SRC/com/example/project/data/LegacyDataNormalizer.java" \
  "$SRC/com/example/project/security/PasswordHasher.java" \
  "$SRC/com/example/project/data/DBHelper.java"

printf '%s\n' 'PASS: final DatabaseContract, DBHelper, PasswordHasher, and LegacyDataNormalizer syntax compiled with -Xlint:all.'
