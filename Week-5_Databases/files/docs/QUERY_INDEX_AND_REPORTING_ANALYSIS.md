# Query, Index, and Reporting Analysis

## User history

```sql
SELECT entry_id, user_id, weight, entry_date, note
FROM weight_entries
WHERE user_id = ?
ORDER BY entry_date DESC, entry_id DESC;
```

The query is restricted by account before ordering. `entry_id DESC` provides deterministic
output for same-date records. The returned Java list is unmodifiable, so callers cannot alter the
result collection and imply that the database changed.

## Latest entry

The latest query uses the same order and `LIMIT 1`. It does not load the complete history and
then discard all but one item.

## Progress summary

The summary query obtains the count, earliest value/date, and latest value/date for one user.
The application computes `latest - first` only after retrieving those endpoints. An account with
no records returns a zero count and null endpoints instead of throwing an error.

## Index decision

The compound index begins with `user_id` because every history query is scoped to one user.
The remaining date and entry ID columns match the order. The verification script uses
`EXPLAIN QUERY PLAN` and confirms that SQLite selects `idx_weight_entries_user_date` for the
history query.

I did not add separate indexes to every column. The application does not search globally by
weight or note, so those indexes would increase storage and write cost without supporting a
current query. The index is tied to an actual access pattern rather than being added for appearance.
