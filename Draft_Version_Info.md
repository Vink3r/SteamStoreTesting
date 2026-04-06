# Steam Automation: Draft Version Documentation

This document provides a high-level overview of the testing suite on the `draft-version` branch. It explains the purpose of each file and how they align with our project's required workflow diagram.

---

## Workflow Diagram Implementation
The following three classes are designed to be run in a linear sequence. Together, they represent the complete End-to-End (E2E) journey as outlined in our project's workflow diagram.

| Step | Test Class | Diagram Mapping | Key Actions |
| :--- | :--- | :--- | :--- |
| **1** | `SearchTests.java` | **Step 1: Discovery** | Home page navigation, search bar interaction, and selecting the target product. |
| **2** | `VerificationTests.java` | **Step 2: Scraper** | Verifying game metadata, scrolling to developer info, and extracting data to text files. |
| **3** | `CartWorkflowTests.java` | **Step 3: Transaction** | Adding the item to the cart, verifying persistence, and executing the removal logic. |

---

## Core Foundation & Configuration
These files act as the "engine" that allows the automation to run across different systems.

* **`BaseTest.java`**: The parent class for all tests. It manages the ChromeDriver lifecycle, initializes explicit waits, and handles automated file I/O for screenshots and data logs.
* **`pom.xml`**: The Maven configuration file. It manages all project dependencies (Selenium 4.21.0, TestNG 7.10.2) and ensures the project builds correctly on any machine.
* **`testng.xml`**: The master execution file. It uses `preserve-order="true"` to ensure the "Big Three" workflow files run in the exact order required to pass data correctly.

---

## Supplementary Unit Tests
These classes provide additional coverage for isolated features on the Steam website.

* **`UrlTitleTests.java`**: Validates the routing controller by checking exact URL structures and page titles.
* **`NavigationTests.java`**: Tests the global header links (About, Support, Sign In) across multiple Steam subdomains.
* **`GenreTests.java`**: Tests the dynamic category menus using case-insensitive CSS selectors to handle dynamic web content.
* **`ReviewTests.java`**: Interacts with the community review system, testing sorting algorithms and JavaScript-based UI triggers.
* **`ScreenshotTests.java`**: Performs visual regression testing by capturing and saving high-resolution images of the storefront.

---

## How to Run this Version
1.  In IntelliJ, right-click the `testng.xml` file.
2.  Select **Run**.
3.  The suite will execute all 8 classes in the proper sequence. Results will appear in the `workflow_screenshots` and `extracted_data` folders.
