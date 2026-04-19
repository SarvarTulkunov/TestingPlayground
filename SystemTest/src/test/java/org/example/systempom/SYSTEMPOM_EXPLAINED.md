# How `systempom` Works — Page Object Model Explained

## What problem does POM solve?

Without POM, test code looks like this everywhere:

```java
driver.findElement(By.name("username")).sendKeys("Admin");
driver.findElement(By.cssSelector("button[type='submit']")).click();
```

If the login button's CSS selector changes, you have to find and fix that line in **every single test** that touches the login page. With 10 tests that's 10 places to change. With 50 tests it becomes a maintenance nightmare.

**Page Object Model** solves this by putting all knowledge about a page (its element locators and how to interact with them) into one dedicated class. Tests never touch raw Selenium — they call methods like `loginPage.clickLogin()`. When the button's selector changes, you fix it in **one place only**.

---

## Package structure overview

```
org.example.systempom/
│
├── pages/          ← One class per screen. Know HOW to interact with the UI.
│   ├── BasePage
│   ├── LoginPage
│   ├── DashboardPage
│   └── AdminPage
│
├── base/           ← Shared JUnit test infrastructure.
│   └── BaseTest
│
├── tests/          ← JUnit tests. Know WHAT to verify. No Selenium here.
│   ├── LoginTest
│   └── AdminTest
│
├── steps/          ← Cucumber (BDD) wiring. Thin bridge between feature files and pages.
│   ├── DriverContext
│   ├── Hooks
│   ├── LoginSteps
│   └── AdminSteps
│
└── runners/
    └── PomCucumberRunner   ← Entry point for Cucumber execution.
```

---

## Layer 1 — Pages

### `BasePage` — the foundation every page inherits

```java
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
}
```

`WebDriver` is the object that controls the browser.
`WebDriverWait` is a helper that retries finding an element for up to 10 seconds before giving up. This is necessary because modern websites load content dynamically — the element you want might not exist in the DOM yet when your code runs.

Every page class `extends BasePage` so they all automatically get `driver` and `wait` without repeating that code.

---

### `LoginPage` — knows everything about the login screen

```
URL: /web/index.php/auth/login
```

**What it stores (locators):**

| Field | What it finds |
|---|---|
| `usernameField` | The username text box |
| `passwordField` | The password text box |
| `loginButton` | The Login submit button |
| `errorMessage` | The red error banner (shown on bad credentials) |

**What it does (methods):**

| Method | What it does |
|---|---|
| `open()` | Tells the browser to navigate to the login URL |
| `enterUsername(text)` | Waits for the field, clears it, types text |
| `enterPassword(text)` | Same for password |
| `clickLogin()` | Waits for the button to be clickable, clicks it |
| `getErrorMessage()` | Waits for the error banner to appear, returns its text |

Notice that `LoginPage` does **not** check whether login succeeded. That responsibility belongs to `DashboardPage`, because a page object should only know about its own screen.

---

### `DashboardPage` — knows everything about the dashboard screen

```
URL: /web/index.php/dashboard/index
```

This is the page the user lands on after a successful login.

**Method:**

| Method | What it does |
|---|---|
| `isLoaded()` | Returns `true` if the browser URL contains `/dashboard` AND the page header is visible. Returns `false` if either condition times out. |

Keeping this separate from `LoginPage` respects the **Single Responsibility Principle**: each page class is only responsible for one screen.

---

### `AdminPage` — knows everything about the System Users admin screen

```
URL: /web/index.php/admin/viewSystemUsers
```

This is the most complex page. It covers two distinct UI states: the **list view** (search + results table) and the **add-user form**.

**Locators split by UI section:**

*List / search area:*
- `usernameSearchInput` — the search box in the filter bar (XPath-scoped to the filter card so it doesn't accidentally match the add-user form's Username field)
- `searchButton` — the Search submit button in the filter bar
- `resultRows` — all rows in the results table
- `noRecordsNotice` — the "No Records Found" text (used to detect an empty result)
- `addButton` — the Add button in the toolbar

*Add-user form:*
- `userRoleDropdown`, `employeeNameInput`, `statusDropdown`, `usernameInput`, `passwordField`, `confirmPasswordField`, `saveButton`, `requiredErrors`

**Special detail — two wait timeouts:**

```java
private final WebDriverWait autocompleteWait; // 20 seconds
```

The Employee Name field triggers an **API call** to search employees. The server response can take longer than a normal DOM update. A separate 20-second wait is used only for that one field; all other interactions use the standard 10-second wait inherited from `BasePage`.

**Key method — `fillEmployeeName`:**

```java
input.sendKeys(searchTerm);                          // type first word
WebElement firstOption = autocompleteWait.until(...) // wait for dropdown
firstOption.click();                                 // click the option directly
```

Clicking the option directly (instead of pressing ARROW_DOWN + ENTER) is critical. The Vue.js combobox does not reliably process keyboard navigation events, and leaving the dropdown open blocks interaction with the fields below it.

---

## Layer 2 — JUnit Tests

### `BaseTest` — shared driver setup for JUnit

```java
public abstract class BaseTest {
    protected WebDriver driver;

    @Before  // runs before every @Test method
    public void setUp() { /* start Chrome */ }

    @After   // runs after every @Test method
    public void tearDown() { /* close Chrome */ }
}
```

`@Before` and `@After` are JUnit annotations. They guarantee a fresh browser for every test and that the browser is always closed even if the test crashes.

---

### `LoginTest` — two JUnit tests for login

```java
public class LoginTest extends BaseTest {

    @Test
    public void validCredentialsRedirectToDashboard() { ... }

    @Test
    public void invalidCredentialsShowErrorMessage() { ... }
}
```

Each `@Test` method:
1. Creates the needed page object(s), passing in `driver` from `BaseTest`
2. Calls page methods to simulate user actions
3. Asserts the expected outcome

There is **no Selenium code** in this class — only page method calls and `assertTrue` / `assertEquals`.

---

### `AdminTest` — three JUnit tests for the admin screen

```java
@Before
public void login() {
    // Log in before every test — equivalent to the Cucumber Background
}
```

| Test method | What it verifies |
|---|---|
| `searchByUsernameShowsMatchingUser` | Searching "Admin" returns a row containing "Admin" |
| `savingEmptyAddFormShowsRequiredErrors` | Submitting the empty add-user form shows validation errors |
| `addNewUserAppearsInUserList` | Filling the full form and saving creates a user visible in search |

---

## Layer 3 — Cucumber / BDD

This layer lets you write tests in plain English (Gherkin) inside `.feature` files and have them execute as real browser tests.

### `DriverContext` — shared browser session for Cucumber

```java
public class DriverContext {
    private WebDriver driver;
    // getter + setter
}
```

In Cucumber, each scenario runs across multiple step classes (e.g. `LoginSteps` handles login, `AdminSteps` handles admin). They all need to use the **same browser window**. `DriverContext` is that shared holder.

Cucumber-PicoContainer (a dependency injection library included in the project) automatically creates **one** `DriverContext` per scenario and injects it into every step class that declares it as a constructor parameter:

```java
public class LoginSteps {
    public LoginSteps(DriverContext context) { ... }  // injected automatically
}
```

---

### `Hooks` — browser lifecycle for Cucumber

```java
@Before  // Cucumber's @Before — runs before each scenario
public void setUp() { /* start Chrome, store in DriverContext */ }

@After   // Cucumber's @After — runs after each scenario
public void tearDown() { /* close Chrome */ }
```

This is the Cucumber equivalent of `BaseTest`. Driver startup/shutdown lives here so that step definition classes (`LoginSteps`, `AdminSteps`) stay focused on business logic only.

> **Important:** These are `io.cucumber.java.Before/After`, not JUnit's. Both exist but serve different frameworks.

---

### `LoginSteps` and `AdminSteps` — thin bridges

Each step method does exactly two things:
1. Calls a page object method
2. Optionally asserts a result

```java
@Given("the user is on the OrangeHRM login page")
public void theUserIsOnTheLoginPage() {
    loginPage = new LoginPage(context.getDriver());
    loginPage.open();                           // one page call — done
}
```

There is no Selenium in these classes. If a step needs to interact with the browser, it goes through a page object.

---

### `PomCucumberRunner` — Cucumber entry point

```java
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",       // where .feature files are
    glue     = "org.example.systempom.steps",       // where step classes are
    plugin   = { "pretty", "html:...", "json:..." } // reporting
)
public class PomCucumberRunner {}
```

This class has no methods. It is just a configuration container that tells JUnit how to run Cucumber. `glue` points to the new `systempom.steps` package (not the original `org.example.steps`), so the POM-based steps are used.

---

## How a full scenario flows end-to-end

Taking the Cucumber scenario **"Add a new system user successfully"** as an example:

```
Feature file (.feature)          Steps class              Page Object
─────────────────────────────    ─────────────────────    ─────────────────────
Background:
  Given the user is on the   →   LoginSteps               LoginPage.open()
        login page
  When enters "Admin"/"admin" →  LoginSteps               LoginPage.enterUsername()
                                                          LoginPage.enterPassword()
  And clicks the login button →  LoginSteps               LoginPage.clickLogin()

Scenario:
  Given admin navigates to    →  AdminSteps               AdminPage.navigateToList()
        Admin page
  When admin clicks Add       →  AdminSteps               AdminPage.clickAdd()
  And fills in role/name/etc  →  AdminSteps               AdminPage.fillUserRole()
                                                          AdminPage.fillEmployeeName()
                                                          AdminPage.fillStatus()
                                                          AdminPage.fillUsername()
                                                          AdminPage.fillPassword()
  And saves the form          →  AdminSteps               AdminPage.clickSave()
  Then user appears in list   →  AdminSteps               AdminPage.navigateToList()
                                                          AdminPage.searchByUsername()
                                                          AdminPage.isUserInResults()
```

Each arrow boundary is where responsibility is handed off. The feature file describes **what** to do in business language. The step class translates Gherkin into method calls. The page object knows **how** to actually do it in Selenium.

---

## Summary — the three rules of POM

| Rule | What it means in practice |
|---|---|
| **One class per screen** | `LoginPage` only knows about the login screen. Dashboard checks live in `DashboardPage`. |
| **No Selenium in tests** | `LoginTest` and step classes call page methods only. `driver.findElement` never appears there. |
| **Locators in one place** | If a CSS selector changes, you update one field in one page class — not every test that uses it. |
