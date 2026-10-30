# Release and maintenance

## Runtime

Build with a full JDK 17+ using the Maven wrapper. A JRE-only or headless-only install may support the store diagnostic but cannot show the Swing app. The shaded JAR contains the two runtime dependencies and JDBC service registration. Launch it with `java -jar target/arns-travel.jar`; a Node runtime is unrelated to this project.

First-time Maven builds require network access to download the pinned distribution and dependencies. Normal application use has no network requirement. A portable native installer/runtime image is not included; the destination computer needs a compatible desktop Java runtime.

## Required checks

1. Run `./mvnw clean verify` and inspect skipped tests as well as failures.
2. Run the explicitly required desktop flow under a real display or Xvfb.
3. Launch the shaded JAR with a fresh private `arns.dataDir`, create a draft, close/reopen, edit quantity, and verify the total.
4. Back up the live store, restore into a separate empty directory, and verify the original row IDs and values.
5. Inspect current dependency advisories and the Maven dependency tree before a release.
6. Verify behavior on each intended OS. Supplied CI jobs do not imply remote results until those jobs have actually run.

The local development environment initially had a headless-only Java installation; desktop verification must run with a headful runtime and a display, not be inferred from a skipped test. Demo drivers live under tests, use an in-memory store and example contact data, and are excluded from the runtime JAR.

## Data migration and rollback

The new store schema is version 1. Initial setup is idempotent and refuses unknown future versions. Take an online snapshot before upgrades. Close all instances before restoration or rollback. Preserve the existing data directory, restore into a separate directory, and use `-Darns.dataDir` to test it before replacing anything. Do not run the original in-memory app expecting it to understand the new H2 data format.

Optional old pipe-delimited exports need an explicit conversion review. No legacy-file importer or silent error-to-empty fallback is included. Requested/cancelled rows are retained; unused draft contacts are removed when drafts are deleted or reassigned. A long-term automated retention policy is not implemented.

## Scope

This release improves the local planning application and documents the original demo catalog. It does not claim provider-certified booking, real-time availability, live pricing, tax calculation, multi-user concurrency, authenticated remote access, or payment processing. All currency labels are CAD examples; no historical or current exchange rate is implied.
