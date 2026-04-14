# Steam Store Page Testing ♨️

This project is an automated testing suite designed to validate the functionality of the Steam Store. Using Java, Selenium WebDriver, and TestNG.

## Tech Stack

- **Language:** Java (JDK 17+)
- **Automation:** Selenium WebDriver
- **Test Framework:** TestNG
- **Build Tool:** Maven
- **Version Control:** Git & GitHub

## Future Roadmap

- Git Action integration
- Additional Tests, additional Games
- Login page verification
- Captcha resolving

## Installation

**1. Clone the repo:**

```bash
git clone https://github.com/Vink3r/SteamStoreTesting.git
```
**2. Install Dependencies:**
```bash
mvn clean install
```
**3. Run regular Test:**
```bash
mvn test
```
**4. Run Test Parallel (classes) or Linear (none)**
```bash
mvn test -DsuiteXmlFile=testng.xml -Dparallel=classes
mvn test -DsuiteXmlFile=testng.xml -Dparallel=none
```
**5. Run Test and Generate Report**

Add this line after `test`
```bash
surefire-report:report
```

## Workflow Diagram

![App Screenshot](https://i.imgur.com/ww03IuW.png)

## Contributors

**Vesper Nguyen | Edward Figueroa | Thuan Van**
