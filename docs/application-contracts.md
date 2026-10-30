# Application and data contracts

There is no HTTP API. The public application boundary is `TravelService`, called by Swing background workers. Reusing that boundary in a future interface should preserve the same checks rather than writing SQL in screen code.

| Operation          | Inputs                                                | Result and guards                                                       |
| ------------------ | ----------------------------------------------------- | ----------------------------------------------------------------------- |
| `catalog().search` | City, category, optional text, sort mode              | Immutable typed options; original demonstration catalog only            |
| `add`              | Catalog ID, traveller, email, date, quantity          | New local draft; validates every input and snapshots the unit price     |
| `edit`             | Existing reservation/version, contact, date, quantity | Draft-only update; increments version; retains the price snapshot       |
| `remove`           | Existing reservation/version                          | Draft-only deletion; cleans unused contacts                             |
| `request`          | Selected drafts and versions                          | Atomic local follow-up marking; sends nothing externally                |
| `cancel`           | Selected follow-up rows and versions                  | Atomic local cancellation; retains history and sends nothing externally |
| `reservations`     | None                                                  | All rows in this OS user's local store; no cross-user sharing           |
| `CsvExport.write`  | A new path, typed rows                                | Quoted UTF-8 CSV with formula-prefix protection; refuses overwrite      |
| `Database.backup`  | A new ZIP path                                        | H2 online backup; private temporary file, exclusive publication         |

## State machine

A draft can be edited or removed. `DRAFT → REQUESTED` marks a local record for supplier follow-up. `REQUESTED → CANCELLED` cancels the local record. Other transitions are rejected. Requested/cancelled rows cannot be edited or deleted through the application. Active totals exclude cancelled rows.

Each mutation checks the expected version in its SQL predicate. Batch state changes commit together or roll back together. A failed/stale edit also rolls back any contact created during that attempt. No version check is based only on a prior screen read.

Dates must be from today through three years ahead at creation/edit/follow-up time. Date comparisons use an injected Java Clock for deterministic testing. Cancelled records may retain historical travel dates. Quantity is 1–365. Names and text have length limits and reject control characters; email is normalized and checked for a basic address structure. Email validation does not verify mailbox ownership or deliverability.

## Schema

- `catalog_items`: stable ID derived from category/city/title, category, title, city, description, illustrative unit price, fixed CAD currency.
- `contacts`: contact ID, full name, normalized email; unique name/email pairs avoid unrelated updates to prior records.
- `reservations`: ID, catalog/contact foreign keys, travel date, quantity, unit-price snapshot, state, version, creation timestamp.
- `schema_version`: supported schema marker. Unknown future versions are rejected.

The line total is unit-price snapshot × quantity. Monetary arithmetic uses integer cents and overflow checks; no `double` or formatted-string parser is involved in reservation totals. Catalog examples originally expressed as doubles are converted once through `BigDecimal.valueOf` at the adapter boundary.

H2 DDL commits implicitly. The initial schema setup is therefore idempotent rather than described as a transactional DDL migration; the version marker is recorded after schema setup succeeds. Future migrations must be explicit, forward-versioned, backup-tested, and compatible with this behavior.
