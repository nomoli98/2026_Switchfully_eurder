# Refactoring changelog

What changed between the original project and the refactored one, and why.
Read this first, then [project-eurder_part1_restApi.md](project-eurder_part1_restApi.md).

> The project was **not compiled or run** during the refactoring. Run `./mvnw clean test` first (see section 6).

## 1. Behaviour changes (decided together)

| ID | Change | Where |
|---|---|---|
| A1 | `PUT /items/{id}` keeps the item id and changes only the fields that are sent. Before, a new item with a new id was stored under the old key. | `ItemService`, `Item` |
| A2 | The third address field is the **city** everywhere. `PostUserRequest.apartment` is now `city` (**JSON field renamed**). City stays optional. | `PostUserRequest`, `UserService` |
| A3 | `PUT /items/{id}` checks `UPDATE_ITEM` instead of `CREATE_ITEM` (same result today: admin only). | `ItemController` |
| A4 | Clean error messages: "This item is not found in the database." (no class names, no "This This"). | `ExistenceException` |
| A5 | Item groups are processed one after the other and stock is reduced as we go, so the same item twice in one order gets the right shipping date. | `EurderService` |
| A6 | Every handled error is logged with `logger.warn`. | `GlobalExceptionHandler` |
| B2 | New admin endpoint `GET /eurders/shipping-today` (one entry per eurder with items shipping today + address). Customer endpoint `my-eurders/today` unchanged. New permission `VIEW_ALL_SHIPPING_TODAY` for ADMIN. | `EurderController`, `Permission`, `Role` |
| B3 | `ItemGroup` stores a snapshot of the item name and unit price. Old orders no longer change when the item changes. The response JSON is unchanged. | `ItemGroup`, `ItemGroupMapper` |
| B4 | Negative stock (backorder) stays. The "stock never below 0" version is described in the documentation (section 8). | `Item.removeStock` |
| B5 | Creating an item: price and stock must be greater than 0 (already the case). Updating: price must be > 0, **stock 0 is allowed**. | `ItemValidator` |
| B7 | An `Authorization` header that is not a UUID now returns **401** (was Spring's default 400). Other invalid UUIDs (path) still return 400. | `GlobalExceptionHandler` |
| – | E-mail must be unique (case-insensitive) → **409 Conflict**. | `UserService`, `ConflictException` |
| – | Phone: optional `+`, 8–15 digits, spaces, dots and dashes allowed (was exactly 10 digits). | `UserValidator` |

Unchanged on purpose: B1 (`POST /users` is admin only), B6 (admin may order and see a report).

## 2. Structure and readability

- **Validators**: `PostItemValidator`, `PutItemValidator`, `PostUserValidator`, `PostEurderValidator` and `Validator` are replaced by `ItemValidator`, `UserValidator`, `EurderValidator` (static methods, no dependencies) and `ValidationUtils` (pure checks).
- Existence checks ("is this item/user/eurder in the database?") moved from the validators to the services.
- **Constructors**: `ItemService(repo)`, `UserService(repo)`, `EurderService(3 repositories)`. Before: up to 5 parameters.
- **`FakeDatabase`** now takes an id function, so repositories no longer repeat `save`. The `update` method and the faulty logging were removed.
- **Domain**
  - `Item`: primitives, setters, `hasStockFor`, `removeStock`.
  - `Role.hasPermission` (the permission list was read from outside).
  - `Eurder.getTotalPrice()`, `ItemGroup.getGroupPrice()` and `Report.getTotalPriceForAllOrders()` are calculated.
  - `Eurder.getItemGroupsShippingOn(date)`.
  - `equals`/`hashCode` of `Item`, `User` and `Eurder` use the `id` only (before: all mutable fields, or none).
- `PutItemRequest` is a record.
- `UserDto` uses `AddressDto` like the other responses (same JSON).
- Controllers: header parameter always named `authorization`, path id named after what it is, wrong Swagger texts fixed (`create users` on items/eurders, "400" for not found, ...).
- Method names fixed: `ItemController.createUser/…` → `createItem`/`updateItem`, `ReportController.createReport` → `getMyReport`, `getEudersShippingToday` → `getMyEurdersShippingToday`.
- `ForbiddenException` moved to the `security` package, next to `UnauthorizedException`.
- `ExistenceException.NotInDatabase(...)` → `notInDatabase(...)`. `RequestException.negativeNumber` is kept; `notPositiveNumber` is new.
- Repository queries are simple: `getByUserId`, `existsByEmailIgnoreCase`. The date filtering moved to `Eurder.getItemGroupsShippingOn`.

## 3. Removed

| What | Why |
|---|---|
| `InsufficientStockException` | Never used (backorder is allowed) |
| `UnauthorizedException.invalidPermission` | Never used |
| `PostUserValidator.existingUser`, `Validator.isValidRole`, `Validator.isInEurderList` | Replaced by service checks / always true |
| `ReportMapper.toDtos`, `ShippingTodayMapper.toDto(Eurder)` | Never used |
| `ItemRepository.updateItem`, `FakeDatabase.update`, the repository constructors taking a list | Never used / replaced by A1 |
| `EurderRepository.getEurdersWithItemsShippedToday`, `getMyEurdersWithItemsShippedToday` | Replaced by `getByUserId` + `Eurder.getItemGroupsShippingOn` |
| `EurderService.getOneEurderById` | Only a test used it (the test now uses the repository) |
| All unused imports (main and test), including `java.lang.classfile…IntegerEntry` in `Item` | Cleanup |
| `commons-beanutils`, `commons-validator`, `asm` in `pom.xml` (+ their version properties) | Not used by the code |

Kept although only tests use them (marked "test only" in `FakeDatabase`): `reset()`, `deleteWithId()`, and the `ItemGroup(item, amount, shippingDate)` constructor.

## 4. Tests

**Adapted**
- `ItemServiceTest`, `UserServiceTest`, `ReportServiceTest`, `EurderServiceTest`: new constructors (no validators).
- `EurderServiceTest`: `getOneEurderById` test now reads from the repository; the price-change test uses `item.setPrice(...)` instead of `itemRepository.updateItem(...)`.
- `UserControllerTest`: phone "too short" is now `04712` and "too long" is `0471234567890123` (the old values `047123456` and `04712345678` are valid now).
- Removed unused imports in `ReportControllerTest`, `ReportServiceTest`, `UserServiceTest`.

**Added**
- `ItemServiceTest`: update keeps id, only price, blank texts, stock 0, price 0 / negative stock, unknown id, create with 0.
- `UserServiceTest`: city stored, duplicate e-mail (any case), formatted and invalid phone numbers.
- `EurderServiceTest`: same item twice, backorder (negative stock), unknown item leaves stock alone, unknown user, snapshot, items shipping today (customer + admin).
- `ItemControllerTest`: partial update with same id, stock 0, price 0 / negative stock, unknown item, no header.
- `UserControllerTest`: 409 duplicate e-mail, formatted phone, header not a UUID → 401, path id not a UUID → 400.
- `EurderControllerTest`: same item twice, item changed after the order, header not a UUID.
- `EurderByShippingDateControllerTest`: admin `GET /eurders/shipping-today` (entries, only today's groups, empty, 403, 401).

## 5. Noticed, not changed

- No admin exists at start-up, and `POST /users` needs an admin, so a freshly started app has no way to create the first user (tests insert one directly). Planned for the auth service / ORM seed.
- The `classDiagram.txt/png` and `projectArchitecture` files in `project_infos` are the originals and are outdated (`apartment` → `city`, item snapshot in `ItemGroup`). The new documentation replaces them.
- A malformed JSON body still returns Spring's default 400 response (not the `{"message": ...}` shape).

## 6. Check after unzipping

1. `./mvnw clean test`. Tell me which tests fail and I will fix them with you.
2. Parts most likely to need a tweak, because I could not run them:
   - the **401 for a non-UUID header** (B7), which depends on how Spring reports header conversion errors;
   - the removed **`asm`** dependency: if you see ASM / "Unsupported class file major version" errors, put it back;
   - the new GPath assertions on `/eurders/shipping-today`.
3. `./mvnw verify` for the 80 % coverage rule.
