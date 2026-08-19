# Week 3 — Software Design and Engineering

This milestone uses my original CS 320 Contact, Task, and Appointment services. The original project worked for the course requirements, but revisiting it gave me several areas I could improve without changing what the application was meant to do.

## Files

- [Software Design narrative](Madison_Parker_CS499_Milestone2_Software_Design_Narrative.docx)
- [`Artifact_Contents/01_ORIGINAL_ARTIFACT_UNMODIFIED/`](Artifact_Contents/01_ORIGINAL_ARTIFACT_UNMODIFIED/) — original CS 320 project
- [`Artifact_Contents/02_ENHANCED_SOFTWARE_DESIGN_ARTIFACT/`](Artifact_Contents/02_ENHANCED_SOFTWARE_DESIGN_ARTIFACT/) — enhanced project
- [Downloadable artifact ZIP](Madison_Parker_CS499_Milestone2_Software_Design_Artifact.zip)

## What I changed

| Area | Original project | Enhanced project |
| --- | --- | --- |
| Project structure | Contact, Task, and Appointment code was kept in separate default-package folders | One Maven project with model, service, validation, repository, and exception packages |
| Validation | The model classes repeated similar field checks | Shared validation methods are used for common rules |
| Missing records | Direct lookups could return `null`, while other operations used general `IllegalArgumentException` errors | Lookups use `Optional`, and missing or duplicate command errors have specific exception types |
| Stored objects | Services stored and returned the same mutable objects they received | Services store copies and return copies |
| Updates | The original services mainly changed one field at a time | Combined updates can validate a copied object before replacing the stored version |
| Appointment dates | Date checks depended on the current system time | A `Clock` can be supplied so time-based tests are repeatable |

The `HashMap` storage from the original project was still useful, so I kept it. The point of this milestone was not to replace everything. I focused on the parts that made the code easier to understand, test, and change safely.

## Testing

The enhanced project includes JUnit tests along with a small Java verification script used during final review. The test source and saved results are kept with the artifact files. The project is still an in-memory classroom application; this milestone does not add a database, login system, or network service.