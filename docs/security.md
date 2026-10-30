# Security model

ARNS is a single-user desktop planner with a local embedded H2 database. It starts no HTTP server, H2 web console, TCP database server, analytics service, or third-party API connection. No credentials, booking payment data, card details, or supplier tokens are collected. A login screen would not protect against another person with access to the same OS account; filesystem and device access are the relevant trust boundary here.

Inputs are validated in the service/model layer. SQL uses prepared statements and fixed operation names. Schema constraints guard quantities, prices, states, and references. Existing versions and states are included in update predicates, and multi-row changes are transactional. Traveller names are rendered as plain text; table labels disable Swing HTML interpretation. CSV fields are quoted and values beginning with spreadsheet formula markers are prefixed with an apostrophe.

The data directory receives owner-only POSIX permissions on supported filesystems, and database, snapshot, and export files are restricted. Windows access relies on the enclosing OS user-profile ACL. The database and snapshots are not encrypted by this application. Use disk encryption, limit physical access, and keep off-device backups protected. CSVs and backups contain personal contact information; the UI identifies this at export.

Snapshots use H2's `BACKUP TO` command and are tested by restoring into a separate database. Snapshot/export publication refuses to overwrite existing paths. Do not import untrusted database snapshots or expose the database file through a shared folder or public host.

The original directory-based class files and generated documentation are removed from the current tree. The runnable build bundles pinned H2 and FlatLaf dependencies. The Maven wrapper's distribution checksum is pinned; retain dependency review and runtime-update checks as part of releases. The local OSV query for the two runtime artifact versions returned no listed advisories at verification time; this is not a complete code or supply-chain audit.

There is no live booking, payment, email sending, or supplier cancellation. The local follow-up state must not be presented as a confirmation number or a purchased booking. Adding providers or a shared/public deployment changes the security model and requires a separate implementation and validation effort.
