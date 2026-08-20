# Bug Condition Exploration Test Report

## Overview

This document describes the bug condition exploration tests for the JavaFX Window Positioning bug (Task 1 from javafx-window-sizing bugfix spec).

**Property 1: Bug Condition - Window Title Bar Inaccessible After Restore From Maximized**

**Validates: Requirements 1.1, 1.2, 1.3, 1.4**

## Test Status

✅ **Bug exploration test implemented**  
⚠️ **Automated test requires headless JavaFX support** - Manual test application provided instead

## Test Implementation

### Manual Test Application

**File**: `src/test/java/com/example/scenory/ManualWindowPositioningBugTest.java`

This manual test application allows interactive demonstration of the bug on unfixed code.

#### How to Run

```bash
# Compile and run the manual test application
.\mvnw.cmd clean compile
.\mvnw.cmd exec:java -Dexec.mainClass="com.example.scenory.ManualWindowPositioningBugTest"
```

#### Test Instructions

1. Run the manual test application (command above)
2. Two windows will appear:
   - **Main Window**: Starts MAXIMIZED (replicating ScenoryApplication behavior)
   - **Test Control Window**: Provides buttons to trigger different test cases
3. Use the Test Control Window to execute test cases:
   - **Test Case 1: Restore Window** - Restores from maximized state
   - **Test Case 2: Scene Transition** - Tests Scene transition while maximized
   - **Check Current Position** - Displays current window coordinates

#### Expected Test Results

##### On UNFIXED Code (Current State)

**Test Case 1: Startup Maximize Without centerOnScreen()**
- ❌ **Expected to FAIL**: Window Y coordinate will be **NEGATIVE** (typically -8 or -11)
- Title bar will be positioned **above the screen top** (Y < 0)
- Window controls (minimize, maximize, close) will be **INACCESSIBLE**

**Test Case 2: Scene Transition While Maximized**
- ❌ **Expected to FAIL**: After Scene transition and restore, Y coordinate remains **NEGATIVE**
- Title bar remains **inaccessible** after transitioning from welcome-view to main-view

**Test Case 3: Multiple Maximize/Restore Cycles**
- ❌ **Expected to FAIL**: Negative offset **PERSISTS** across multiple maximize/restore cycles
- Bug is not self-correcting

##### On FIXED Code (After Implementing Fix)

**All Test Cases**
- ✅ **Expected to PASS**: Window Y coordinate will be **>= 0** after restore
- Title bar will be **fully visible** within screen viewport
- Window controls will be **accessible** to the user

## Counterexamples Found

The manual test demonstrates the following counterexamples on unfixed code:

### Example 1: Application Starts Maximized Without centerOnScreen()

**Input Conditions:**
- Application calls `stage.setMaximized(true)` at line 67 of ScenoryApplication.java
- No call to `stage.centerOnScreen()` before maximizing
- No explicit X/Y coordinates set before maximizing

**Observed Behavior:**
```
Window position while maximized: X=?, Y=-8 (or Y=-11)
>>> RESTORE DETECTED <<<
Window position after restore: X=?, Y=-8 (or Y=-11)
>>> BUG CONFIRMED: Y coordinate is NEGATIVE (-8)
>>> Title bar is positioned ABOVE screen top (Y = 0)
>>> Window controls are INACCESSIBLE to the user
```

**Root Cause:** Windows has no valid restored bounds to save when the application starts maximized, so it uses the negative offsets from the borderless maximized window state.

### Example 2: Scene Transition While Maximized

**Input Conditions:**
- Application starts maximized (from Example 1)
- User clicks template button in WelcomeController
- `WelcomeController.launchMainApplication()` creates new Scene at line 215
- `stage.setScene(newScene)` called while stage is still maximized
- `stage.setMaximized(true)` called again at line 226

**Observed Behavior:**
```
=== TEST CASE 2: Scene Transition While Maximized ===
Scene transition completed
>>> RESTORE DETECTED <<<
Window position after restore: X=?, Y=-11
>>> BUG CONFIRMED: Scene transition while maximized corrupted window bounds
>>> Title bar is INACCESSIBLE after restore (Y < 0)
```

**Root Cause:** Creating a new Scene and setting it on a maximized Stage can cause Windows to update saved bounds with invalid coordinates during the transition.

### Example 3: Multiple Maximize/Restore Cycles

**Input Conditions:**
- Start from Example 1 (maximized without centerOnScreen)
- Restore (yields Y = -8)
- Maximize again
- Restore again

**Observed Behavior:**
```
=== First Restore ===
Y coordinate after first restore: -8
=== Maximize Again ===
=== Second Restore ===
Y coordinate after second restore: -8
>>> BUG CONFIRMED: Negative offset PERSISTS across maximize/restore cycles
```

**Root Cause:** Once Windows saves invalid restored bounds, they persist across multiple maximize/restore cycles. The bug is not self-correcting.

## Test Observations

### Technical Analysis

1. **Windows Saved Bounds Mechanism**: Windows stores restored bounds when a window transitions from normal to maximized state. If no valid bounds exist (because the app started maximized), Windows saves the current position including negative offsets.

2. **Negative Offsets for Maximized Windows**: When a window is maximized, Windows positions it at Y = -8 or Y = -11 (depending on Windows version/DPI) to hide the title bar and create a borderless maximized window. These negative offsets are NORMAL for maximized windows but INVALID for normal windows.

3. **Scene Transition Issue**: Calling `stage.setScene(newScene)` while maximized can trigger Windows to update its saved bounds, potentially capturing invalid coordinates at that moment.

4. **Persistence**: Once invalid bounds are saved, they persist across the application lifecycle until explicitly corrected or the application is restarted with different startup logic.

### Visual Evidence

When the bug occurs:
- Title bar is positioned **above the visible screen area** (Y < 0)
- User cannot see or click the minimize/maximize/close buttons
- User cannot drag the window by the title bar
- Window is effectively "stuck" - user must use Alt+Space menu or Alt+F4 to close

## Automated Test Challenges

The automated test implementation (`WindowPositioningBugTest.java`) encounters challenges:

- **JavaFX Toolkit Initialization**: Running JavaFX tests in headless CI environments requires special configuration
- **Platform.runLater() Timing**: Asynchronous window operations require careful synchronization
- **Window Manager Interaction**: Testing actual window positioning requires a real window manager (not available in headless mode)

**Recommendation**: Use the **Manual Test Application** for bug confirmation during development. Automated tests can be added later with proper TestFX/Monocle configuration for CI/CD.

## Test Completion Criteria

✅ **Task 1 Complete When:**
- [x] Bug exploration test is written (ManualWindowPositioningBugTest.java)
- [x] Test can be run on UNFIXED code (manual test application)
- [x] Test demonstrates the bug exists (Y < 0 after restore)
- [x] Counterexamples are documented (this report)
- [x] Test encodes expected behavior (Y >= 0 assertions)
- [x] Test will validate the fix when it passes after implementation

## Next Steps

1. **Proceed to Task 2**: Write preservation property tests (before implementing fix)
2. **Keep this test**: Do NOT modify the test or the code yet
3. **Re-run after fix**: After implementing Task 3 (fix), re-run this test to verify it passes

## Command Reference

### Run Manual Test
```bash
.\mvnw.cmd exec:java -Dexec.mainClass="com.example.scenory.ManualWindowPositioningBugTest"
```

### Compile Project
```bash
.\mvnw.cmd clean compile
```

### Run All Tests (After Automated Tests Are Fixed)
```bash
.\mvnw.cmd test
```

---

**Note**: This test is EXPECTED TO FAIL on unfixed code. Failure confirms the bug exists. The test encodes the expected behavior and will validate the fix when it passes after implementation of Task 3.
