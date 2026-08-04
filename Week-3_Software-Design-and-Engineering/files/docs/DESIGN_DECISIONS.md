# Design Decisions

## Keep the artifact focused

The enhancement does not turn a small in-memory project into a database or web application. The original requirement is still recognizable: contacts, tasks, and appointments are stored by unique ID and validated before use. The changes improve the design boundaries around that requirement.

## Use packages rather than one default namespace

The model classes own record state, the service classes own collection operations, the validation utility owns shared field rules, the repository interface describes common operations, and the exception package distinguishes expected service failures. This separation makes responsibilities visible without adding a large framework.

## Preserve `HashMap` storage

A `HashMap` remains appropriate because unique identifiers are the primary access key. Replacing it only to make the enhancement appear larger would weaken the connection between the requirement and the design.

## Return snapshots from service lookups

The original services returned the mutable object stored in the map. A caller could then change a record without using the service. The enhanced services store copies and return copies. Model setters remain available, but changes to a returned snapshot do not alter service state.

## Make multi-field changes atomic

The service creates and validates a copy before replacing a stored record. If one requested value is invalid, the original stored object remains unchanged. This avoids a partial update where one field changes before a later validation error occurs.

## Use `Optional` for lookup and exceptions for commands

`findById` returns `Optional.empty()` when a record is absent. Update and delete commands throw `RecordNotFoundException` because those commands cannot complete without an existing record. The return type makes the lookup policy explicit, while the command failure remains specific and testable.

## Inject time only where it improves testability

The appointment constructor still offers the original three-argument API. A second constructor accepts `Clock` so tests can define the present moment exactly. This removes timing-dependent boundary failures without changing how normal callers create appointments.
