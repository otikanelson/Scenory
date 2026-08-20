# Preservation Property Tests - Manual Execution Instructions

## Task 2: Write preservation property tests (BEFORE implementing fix)

### Test File Location
`src/test/java/com/example/scenory/WindowPreservationPropertyTest.java`

### Why Manual Execution is Required
The preservation property tests require actual JavaFX window operations (creating stages, moving/resizing windows, maximizing/minimizing) which cannot run in:
- Headless environments (Maven on command line without display)
- CI/CD pipelines without X server
- Automated test runners without window manager access

These tests MUST be run in an IDE (IntelliJ IDEA, Eclipse, VS Code) with a graphical display environment.

### How to Run in IDE

#### IntelliJ IDEA:
1. Open the project in IntelliJ IDEA
2. Navigate to `src/test/java/com/example/scenory/WindowPreservationPropertyTest.java`
3. Right-click on the class name or individual test methods
4. Select "Run 'WindowPreservationPropertyTest'" or "Run 'methodName()'"
5. Ensure your IDE is configured to run JavaFX applications (VM options may be needed)

#### Eclipse:
1. Open the project in Eclipse
2. Navigate to the test file
3. Right-click and select "Run As > JUnit Test"
4. Ensure JavaFX runtime is properly configured

#### VS Code:
1. Open the project in VS Code
2. Install "Test Runner for Java" extension if not already installed
3. Click the test beaker icon next to the class or method
4. Select "Run Test"

### VM Options (if needed)
If tests fail to initialize JavaFX, add these VM options:
```
--add-opens javafx.graphics/javafx.stage=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

### Expected Test Results on UNFIXED Code

According to the task specification, **ALL 5 tests should PASS** on unfixed code:

1. ✅ **Property 2a: Manual Window Move Preservation**
   - Tests that manually moving a window to position (X, Y) preserves that position
   - Validates Requirements 3.1, 3.2
   - Expected: PASS (10 property-based test tries)

2. ✅ **Property 2b: Manual Window Resize Preservation**
   - Tests that manually resizing a window to size (W, H) preserves that size
   - Validates Requirements 3.1, 3.2, 3.5
   - Expected: PASS (10 property-based test tries)

3. ✅ **Property 2c: Maximize/Restore From Normal State Preservation**
   - Tests that maximize/restore cycle from normal state returns to starting position
   - This is DIFFERENT from the bug (starts in normal state, not maximized)
   - Validates Requirements 3.1, 3.2, 3.3
   - Expected: PASS (10 property-based test tries)

4. ✅ **Property 2d: Minimize/Restore Preservation**
   - Tests that minimize/restore is an identity operation (position unchanged)
   - Validates Requirements 3.1, 3.2
   - Expected: PASS (10 property-based test tries)

5. ✅ **Property 2e: Subsequent Maximize/Restore Stability**
   - Tests that multiple maximize/restore cycles use same restored position consistently
   - Validates Requirements 3.1, 3.2, 3.3
   - Expected: PASS (10 property-based test tries)

### What These Tests Verify

These preservation tests verify that **non-buggy window operations** work correctly:
- Manual window positioning (NOT automatic startup positioning)
- Manual window resizing
- Maximize/restore cycles **starting from normal state** (NOT starting maximized like the bug)
- Minimize/restore operations
- Multiple maximize/restore stability with valid bounds

### Distinction from Bug Condition

**The bug occurs when:**
- Application starts maximized WITHOUT calling centerOnScreen() first
- Scene transitions occur WHILE maximized

**These preservation tests cover:**
- Operations AFTER establishing valid window bounds
- Manual user operations (moves, resizes)
- Normal maximize/restore cycles (NOT startup maximize)

### After Running Tests

1. Verify all 5 tests pass
2. Check console output for actual vs expected positions (should match within tolerance)
3. Document any failures (there should be none on unfixed code)
4. If tests fail, it may indicate:
   - Window manager differences (OS-specific behavior)
   - Timing issues (may need to increase sleep times)
   - Display scaling settings affecting measurements

### Completing Task 2

Once you've confirmed all 5 preservation tests PASS in your IDE:
1. The baseline behavior is established
2. Task 2 is complete
3. After implementing the fix (Task 3), re-run these same tests
4. They should still PASS, proving no regressions were introduced

## Troubleshooting

### "Toolkit not initialized" error
- Add VM options listed above
- Ensure JavaFX runtime is in classpath
- Try running a simple JavaFX application first to verify setup

### Tests timeout
- Increase timeout values in test methods
- Check if windows are actually appearing (may be hidden off-screen)
- Verify window manager is running

### Position/size assertions fail
- Check display scaling settings (150%, 200%, etc.)
- Adjust tolerance values if needed for your display
- Some window managers add padding/borders differently

### Property tests shrink to small values
- This is normal jqwik behavior when trying to find minimal failing example
- Should not occur if tests pass
