# ARNS development context

The project is a Java 17 Swing desktop application with an embedded H2 database. README.md, docs/application-contracts.md, and docs/security.md describe the current implementation. The old React/Node/MongoDB description was inaccurate.

- Keep the runnable entry point arns.App and Maven src/main / src/test layout.
- Use TravelService as the UI's application boundary. Never parse prices from presentation strings or mutate private fields with reflection.
- Keep money in integer cents, use explicit quantity units, and retain reservation unit-price snapshots.
- Every update/delete must check both expected version and legal state. Batch state transitions must be atomic.
- Keep SQL parameterized. Validate inputs independently of screen controls.
- Run storage operations off the Swing event thread; update Swing widgets only on that thread.
- Treat the original catalog as demonstration content. No pretend checkout or supplier confirmation.
- Preserve local user data, online backup correctness, and future-schema rejection.
- Disable Swing HTML rendering for user-controlled display strings and preserve CSV formula protection.
- Keep generated databases, compiled classes, target output, traveller details, and secrets out of Git.
- Run verify, explicitly require the desktop test when a display is available, smoke-test the shaded JAR, and inspect skipped checks before reporting success.
- Test-only demo drivers must not be included in the runnable application.
