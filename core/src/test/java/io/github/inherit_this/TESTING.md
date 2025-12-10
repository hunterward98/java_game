# Testing Guide

This project uses **JUnit 5** (Jupiter) for unit testing. Tests are located in `core/src/test/java/`.

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests "io.github.inherit_this.entities.PlayerStatsTest"
```

### Run Specific Test Method
```bash
./gradlew test --tests "io.github.inherit_this.entities.PlayerStatsTest.testLevelUp"
```

### Run Tests with Detailed Output
```bash
./gradlew test --info
```

### View Test Report
After running tests, open the HTML report:
```
core/build/reports/tests/test/index.html
```

## Test Structure

Tests follow standard JUnit 5 conventions:
- Test classes are in `core/src/test/java/`
- Test class names end with `Test` (e.g., `PlayerStatsTest`)
- Test methods are annotated with `@Test`
- Use `@BeforeEach` for setup before each test
- Use `@DisplayName` for descriptive test names

## Example Test

```java
package io.github.inherit_this.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsTest {
    private PlayerStats stats;

    @BeforeEach
    void setUp() {
        stats = new PlayerStats();
    }

    @Test
    @DisplayName("Player should start at level 1")
    void testInitialLevel() {
        assertEquals(1, stats.getLevel());
    }
}
```

## Writing New Tests

1. Create test file in appropriate package under `core/src/test/java/`
2. Name the test class `[ClassName]Test`
3. Import JUnit 5 assertions: `import static org.junit.jupiter.api.Assertions.*;`
4. Add `@Test` annotation to test methods
5. Use descriptive names and `@DisplayName` annotations

## Continuous Integration

To run tests as part of your build process:
```bash
./gradlew clean build
```

This will compile, run all tests, and build the project.

## Current Test Coverage

- **PlayerStatsTest**: Tests for XP system, leveling, stat management, and damage calculation
  - Initial state validation
  - Leveling up mechanics
  - Stat increase on level up
  - Health/Mana/Stamina management
  - Damage scaling

## Adding More Tests

Good candidates for future tests:
- **InventoryTest**: Item placement, stacking, gold management
- **LootTableGeneratorTest**: Scaling loot generation
- **EquipmentTest**: Equipment slot management
- **CombatManagerTest**: Combat calculations
- **SaveManagerTest**: Save/load functionality

## Dependencies

Testing dependencies (configured in `core/build.gradle`):
- JUnit Jupiter 5.10.1 - Testing framework
- Mockito 5.7.0 - Mocking framework
- LibGDX headless backend - For testing without graphics

## Best Practices

1. **Test one thing per test** - Each test should verify a single behavior
2. **Use descriptive names** - Test names should describe what they test
3. **Follow AAA pattern** - Arrange, Act, Assert
4. **Don't test LibGDX internals** - Focus on game logic
5. **Use headless backend** - Tests don't need graphics
6. **Keep tests fast** - Unit tests should run quickly
7. **Make tests deterministic** - No random values in tests

## Troubleshooting

### Tests won't compile
- Make sure test dependencies are in `core/build.gradle`
- Check that JUnit Platform is enabled: `test { useJUnitPlatform() }`
- Verify imports use `org.junit.jupiter.*` (not JUnit 4)

### Tests won't run
- Run `./gradlew clean test` to force rebuild
- Check test class/method names follow conventions
- Verify `@Test` annotations are present

### LibGDX initialization errors
- Use headless backend in tests
- Mock LibGDX components (Gdx.app, Gdx.files, etc.) if needed
