# Algorithm Complexity and Trade-Offs

Let `n` be the number of records stored in a service, `m` be the number of records that match a search, and `c` be the current `HashMap` capacity. Oracle documents iteration over a `HashMap` collection view as proportional to capacity plus size. With the default load policy and capacity proportional to the record count, `O(c + n)` is conventionally simplified to `O(n)`.

| Operation | Time | Additional result space | Reason |
|---|---:|---:|---|
| Add, find, update, or delete by unique ID | Average O(1) | O(1) | Identifier is the `HashMap` key |
| Contact last-name search | O(c + n + m log m) | O(m) | Traverse the map view, copy matches, then sort them |
| Task keyword search | O(c + n + m log m) | O(m) | Traverse the map view once, check two fields, copy matches, then sort them |
| Appointment date-range filter | O(c + n + m log m) | O(m) | Traverse the map view, copy inclusive matches, then sort them |

String matching also depends on the length of the text being compared. The table treats the short, requirement-limited fields as bounded values so the record count remains the dominant factor.

## Why retain `HashMap`

The original artifact is organized around unique record IDs. A `HashMap` remains a strong fit for those exact-ID operations. Replacing it with a sorted structure would add ordering but could make the main lookup path slower or more complex.

## Why use a linear scan for searches

The project stores a small in-memory collection and allows records to change. A linear scan is simple, correct, and keeps one source of truth. Building secondary indexes for last names, keywords, and dates would improve repeated searches at larger scale but would also add memory use and require every update to keep several structures synchronized.

## Why sort only the matches

The methods filter first and sort `m` matching records instead of sorting all `n` records before filtering. When a search is selective, sorting the smaller result reduces unnecessary comparisons. Comparator tie-breakers guarantee deterministic output even when names or dates are equal.

## Why return immutable snapshots

The result list is immutable, and each element is a copy of the stored record. This uses O(m) result space but prevents the caller from changing the service's list or internal objects through a search result. The trade-off favors a clear boundary and reliable tests over returning shared mutable state.

## Constant-factor performance polish

The final implementation reuses comparator chains as class constants instead of rebuilding them for every query. It also returns Java 17's unmodifiable `Stream.toList()` result directly rather than copying that result through `List.copyOf`. Appointment range checks and ordering compare primitive epoch-millisecond values, which avoids repeatedly allocating defensive `Date` copies during an O(m log m) sort. These decisions reduce temporary allocations without changing the documented asymptotic bounds or weakening the defensive copies of returned records.

## Scaling decision

For the current artifact, a capacity-and-size traversal plus O(m log m) sorting is appropriate. If the collection moved to persistent storage or grew enough for search latency to matter, a next step would be an indexed database query or maintained secondary indexes selected from measured access patterns. A date-oriented `NavigableMap` could locate a range in O(log n + m), while task keyword retrieval would require an inverted index and tokenization policy. The current narrative does not claim that the in-memory approach is optimal for every scale.
