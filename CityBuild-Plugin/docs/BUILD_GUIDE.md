# CityBuild Plugin - Local Build & Development Guide

Quick setup guide for local development and building the CityBuild Plugin.

---

## Prerequisites

- **Java 21+** - Required for compilation
  - Download: https://adoptium.net/temurin/releases/?version=21
  - Verify: `java -version` (should show Java 21.x.x)

- **Maven 3.9+** (optional, uses Maven Wrapper by default)
  - Not required if using `./mvnw` commands below

---

## Build Commands

### Using Maven Wrapper (Recommended - No Maven Installation Needed)

**Windows (PowerShell/CMD):**
```powershell
# Full build with tests
.\mvnw.cmd clean package

# Quick build without tests
.\mvnw.cmd -B clean package -DskipTests

# Clean only (remove target/)
.\mvnw.cmd clean
```

**Linux/macOS:**
```bash
# Full build with tests
./mvnw clean package

# Quick build without tests
./mvnw -B clean package -DskipTests

# Clean only
./mvnw clean
```

### Using Installed Maven

If Maven is already installed, use `mvn` directly:
```bash
mvn -B clean package -DskipTests
```

---

## Common Build Commands

### Full Build (Recommended before commits)
```bash
./mvnw clean package
```
- Cleans `target/` directory
- Compiles all source code
- Runs all JUnit tests
- Generates JAR artifact at `target/CityBuildPlugin-*.jar`

### Compile Only
```bash
./mvnw clean compile
```
- Fastest option - only checks for syntax errors
- Useful for quick validation while coding

### Skip Tests (for CI/testing without full suite)
```bash
./mvnw -B clean package -DskipTests
```
- Compiles and packages without running JUnit tests
- Used by GitHub Actions workflow

### Run Specific Test
```bash
./mvnw test -Dtest=AdminPlotCommandHandlerTest
```

### View Test Coverage
```bash
./mvnw clean test
```
Then check: `target/site/jacoco/index.html` (if JaCoCo configured)

---

## Troubleshooting Build Issues

### Java Version Mismatch
```
Error: [COMPILATION ERROR] java.lang.UnsupportedClassVersionError

Fix:
1. Verify Java 21: java -version
2. Set JAVA_HOME:
   Windows: set JAVA_HOME=C:\Program Files\OpenJDK\jdk-21.x.x
   Linux:   export JAVA_HOME=/path/to/jdk-21
3. Rerun: ./mvnw clean compile
```

### Maven Wrapper Not Found
```
Error: '.\mvnw.cmd' is not recognized

Fix:
1. Ensure you're in CityBuild-Plugin root directory
2. Check that .mvn/wrapper/ directory exists
3. If missing, download Maven wrapper files from:
   https://github.com/apache/maven-wrapper
```

### Compilation Errors

**"cannot find symbol"** error:
- Check imports are present in the Java file
- Example: Adventure API requires explicit imports:
  ```java
  import net.kyori.adventure.text.Component;
  import net.kyori.adventure.text.format.NamedTextColor;
  ```

**"incompatible types"** error:
- Check method signatures match their calls
- Verify PlotGenerator, PlotManager, AdminPlotCommandHandler interfaces are aligned

### Clear Cache & Rebuild
```bash
./mvnw clean
rm -rf .m2/repository/com/citybuild  # Clear local cache
./mvnw clean compile
```

---

## Development Workflow

### 1. Make Code Changes
Edit Java files in `src/main/java/`

### 2. Quick Syntax Check
```bash
./mvnw clean compile
```

### 3. Run Tests Locally
```bash
./mvnw test
```

### 4. Full Build Before Committing
```bash
./mvnw clean package
```

### 5. Commit & Push
```bash
git add src/
git commit -m "Feature: Description"
git push origin main
```
- GitHub Actions will run full build automatically
- Check Actions tab for build status

---

## Project Structure

```
CityBuild-Plugin/
├── src/main/java/com/citybuild/
│   ├── CityBuildPlugin.java
│   ├── commands/
│   │   ├── CityBuildCommand.java
│   │   ├── AdminCommandHandler.java
│   │   └── AdminPlotCommandHandler.java
│   ├── managers/
│   │   ├── PlotManager.java
│   │   ├── EconomyManager.java
│   │   └── WorldManager.java
│   ├── listeners/
│   │   ├── PlotProtectionListener.java
│   │   └── PlayerListener.java
│   ├── model/
│   │   └── PlotData.java
│   ├── utils/
│   │   └── PlotGenerator.java
│   └── services/
│       └── EconomyService.java
│
├── src/test/java/com/citybuild/
│   ├── commands/
│   └── managers/
│
├── src/main/resources/
│   ├── plugin.yml
│   ├── config.yml
│   └── messages.yml
│
├── pom.xml
├── mvnw.cmd                    # Windows Maven Wrapper
├── mvnw                        # Linux/macOS Maven Wrapper
├── .mvn/wrapper/               # Maven Wrapper files
├── target/                     # Build output (generated)
│   └── CityBuildPlugin-*.jar
└── docs/
    ├── ADMIN_COMMANDS.md
    ├── DEPLOYMENT_CHECKLIST.md
    └── BUILD_GUIDE.md (this file)
```

---

## Dependencies

### Plugin Dependencies (in pom.xml)
- **Paper API 1.21.1** - Minecraft server API
- **GSON 2.10.1** - JSON serialization
- **Adventure API** - Text components (included via Paper)

### Test Dependencies
- **JUnit 5.10.2** - Unit testing framework
- **Mockito 5.11.0** - Mocking library
- **Maven Surefire 3.2.5** - Test runner

---

## GitHub Actions CI/CD

Automated builds run on every push via `.github/workflows/build.yml`:

1. Checkout code
2. Set up JDK 21
3. Build: `mvn -B clean package -DskipTests`
4. Upload JAR artifact (30 days retention)
5. Create GitHub Release (on tag push)

Check build status: https://github.com/[YOUR_REPO]/actions

---

## IDE Setup (IntelliJ IDEA / Eclipse)

### IntelliJ IDEA
1. File → Open → CityBuild-Plugin folder
2. IntelliJ auto-detects pom.xml
3. Right-click → Maven → Reload Project
4. Build → Build Project

### Eclipse
1. File → Import → Existing Maven Projects
2. Select CityBuild-Plugin folder
3. Click Finish
4. Project → Build Project

### VS Code
1. Install "Extension Pack for Java" (Microsoft)
2. Install "Maven for Java" (Microsoft)
3. VS Code auto-detects pom.xml
4. Compile: Ctrl+Shift+B or `./mvnw clean compile`

---

## Performance Tips

### Faster Builds
```bash
# Skip tests during development
./mvnw -DskipTests clean package

# Skip documentation generation
./mvnw -DskipTests -Dmaven.javadoc.skip=true clean package

# Use offline mode (if dependencies cached)
./mvnw -o clean compile
```

### Incremental Compilation
After initial build, only changed files compile:
```bash
./mvnw compile  # Recompiles only changed files
```

---

## Advanced: Custom Build Profiles

You can define custom profiles in `pom.xml` for different environments:

Example usage:
```bash
./mvnw clean package -P production
./mvnw clean package -P development
```

(Contact project maintainer for profile definitions)

---

## Troubleshooting Maven Wrapper

### Maven Wrapper won't run
- Ensure `.mvn/wrapper/` directory exists
- Verify `mvnw` has execute permission (Linux/macOS):
  ```bash
  chmod +x mvnw
  ```

### Need to update Maven version
Edit `.mvn/wrapper/maven-wrapper.properties`:
```properties
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
```

---

## Questions & Support

For build issues:
1. Check this guide (BUILD_GUIDE.md)
2. Run with verbose flag: `./mvnw -X clean compile`
3. Check GitHub Issues: [PROJECT_REPO]/issues
4. Check server logs: `/logs/latest.log`

---

**Last Updated:** 2026-04-23  
**Maven Version:** 3.9.6  
**Java Version:** 21+  
**Status:** ✅ Ready for development
