# JavaFX Window Sizing Bugfix Design

## Overview

This bugfix addresses a critical window positioning issue in the JavaFX application where the window title bar becomes inaccessible after restoring from maximized state on Windows. The bug occurs because:

1. The application starts maximized (`stage.setMaximized(true)`) without first establishing valid restored bounds
2. Windows saves negative offset coordinates (Y = -8 or Y = -11, which are normal for borderless maximized windows) as the restored bounds
3. Creating a new Scene during the welcome→main view transition while maximized can cause Windows to apply invalid saved bounds when restoring
4. When the user clicks restore, Windows applies these negative offsets to the normal window frame, pushing the title bar above the visible screen

The fix strategy involves establishing explicit valid restored bounds before maximizing and handling Scene transitions properly to preserve valid window bounds.

## Glossary

- **Bug_Condition (C)**: The condition that triggers the bug - when the application starts maximized without valid restored bounds or transitions Scenes while maximized, causing negative offset coordinates to be saved
- **Property (P)**: The desired behavior - window title bar must remain fully visible and accessible within screen viewport after restoring from maximized state
- **Preservation**: Existing window positioning behavior (manual moves/resizes, multiple maximize/restore cycles, maximized display) that must remain unchanged by the fix
- **stage**: The JavaFX `Stage` object representing the application window in `ScenoryApplication.java`
- **restored bounds**: The X, Y, width, and height values that Windows uses when transitioning from maximized to normal window state
- **negative offset coordinates**: Y values like -8 or -11 that Windows uses for borderless maximized windows but are invalid for normal windows
- **Scene transition**: The process in `WelcomeController.launchMainApplication()` where a new Scene object is created and set on the Stage during the welcome→main view transition

## Bug Details

### Bug Condition

The bug manifests when the application starts maximized without establishing valid restored bounds first, or when a new Scene is created and set on the Stage during the welcome→main view transition while the window is still maximized. The system saves negative offset coordinates (Y = -8 or -11) as restored bounds, which are normal for borderless maximized windows but invalid for normal windows.

**Formal Specification:**
```
FUNCTION isBugCondition(input)
  INPUT: input of type WindowState
  OUTPUT: boolean
  
  RETURN (input.startedMaximized == true
         AND (input.centerOnScreenCalled == false OR input.explicitPositionSet == false))
         OR (input.sceneTransitionWhileMaximized == true
             AND input.restoredBoundsPreserved == false)
         AND input.savedYCoordinate < 0
         AND input.userClickedRestore == true
END FUNCTION
```

### Examples

- **Example 1 - Start Maximized Without Setup**: Application calls `stage.setMaximized(true)` in `ScenoryApplication.start()` without calling `stage.centerOnScreen()` first. Windows saves Y = -8 as restored bounds. When user clicks restore button, title bar appears at Y = -8 (above screen top at Y = 0).

- **Example 2 - Scene Transition While Maximized**: Application starts maximized, user clicks template button, `WelcomeController.launchMainApplication()` creates new Scene and calls `stage.setScene(newScene)` while stage is still maximized. Windows may update saved bounds during this transition. When user clicks restore, title bar is inaccessible.

- **Example 3 - Multiple Maximize/Restore Cycles**: After initial bug occurs, subsequent maximize/restore cycles continue to use the invalid saved bounds (Y = -8), making the bug persistent until application restart.

- **Edge Case - Manual Window Move Before Maximize**: User manually moves window to valid position before maximizing. Expected behavior: restore should use the manually set position, not negative offsets.

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- Application must continue to start in maximized state as designed
- Manual window moves and resizes in normal (non-maximized) state must continue to be preserved correctly
- Multiple maximize/restore cycles must continue to remember the last valid restored position and size
- Font loading, stylesheet application, and icon setting during startup must remain unaffected
- Minimum window dimensions (setMinWidth/setMinHeight) enforcement must remain unchanged

**Scope:**
All window operations that do NOT involve the initial startup maximization or Scene transitions should be completely unaffected by this fix. This includes:
- Manual dragging of window title bar to new positions
- Manual resizing of window edges in normal state
- Minimize/restore operations (as opposed to maximize/restore)
- Window operations after valid restored bounds are established

## Hypothesized Root Cause

Based on the bug description and code analysis, the most likely issues are:

1. **Missing Initial Positioning Before Maximize**: In `ScenoryApplication.start()`, the code calls `stage.setMaximized(true)` at line 67 without first calling `stage.centerOnScreen()` or setting explicit X/Y coordinates. Windows has no valid restored bounds to save, so it uses the negative offsets from the maximized state.

2. **Scene Transition Timing Issue**: In `WelcomeController.launchMainApplication()` at lines 215-217, a new Scene is created and set on the Stage while the Stage is still maximized. This Scene replacement may trigger Windows to update the saved bounds, potentially capturing invalid coordinates.

3. **No Explicit Bounds Preservation**: The code does not explicitly save and restore window bounds across the Scene transition, relying on Windows' automatic bounds management which is unreliable during Scene transitions.

4. **Order of Operations**: The sequence of `stage.setScene()` followed by `stage.setMaximized(true)` in the transition code may not give Windows enough time to establish valid bounds before maximizing.

## Correctness Properties

Property 1: Bug Condition - Valid Restored Bounds After Maximize/Restore

_For any_ application startup where the Stage is set to maximized, or any Scene transition that occurs while maximized, the window management system SHALL ensure that valid positive Y coordinates (≥ 0) are established as restored bounds before maximizing, such that when the user clicks the restore button, the window title bar appears fully visible within the screen viewport with Y coordinate ≥ 0.

**Validates: Requirements 2.1, 2.2, 2.3, 2.4**

Property 2: Preservation - Non-Startup Window Operations

_For any_ window operation that is NOT the initial startup maximization or a Scene transition (manual moves, manual resizes, minimize/restore, subsequent maximize/restore with valid bounds), the fixed code SHALL produce exactly the same window positioning behavior as the original code, preserving all existing manual window management functionality.

**Validates: Requirements 3.1, 3.2, 3.3**

## Fix Implementation

### Changes Required

Assuming our root cause analysis is correct:

**File**: `src/main/java/com/example/scenory/ScenoryApplication.java`

**Function**: `start(Stage stage)`

**Specific Changes**:

1. **Add centerOnScreen() Before Maximize**: Insert `stage.centerOnScreen();` after `stage.setScene(scene);` (line 61) and before `stage.setMaximized(true);` (line 67) to establish valid restored bounds
   - This ensures Windows has positive X/Y coordinates to save as restored bounds
   - centerOnScreen() calculates screen dimensions and positions window at center

2. **Alternative: Set Explicit Position**: Instead of centerOnScreen(), set explicit X/Y coordinates: `stage.setX(100); stage.setY(100);` before maximizing
   - Provides more control over initial position
   - Ensures positive coordinate values

3. **Show Before Maximize**: Move `stage.show();` (line 68) to occur before `stage.setMaximized(true);` if not already
   - Some JavaFX versions require the stage to be shown before maximizing for proper bounds calculation
   - Allows window manager to establish default bounds

**File**: `src/main/java/com/example/scenory/controller/WelcomeController.java`

**Function**: `launchMainApplication(Project project)`

**Specific Changes**:

4. **Preserve Maximized State During Transition**: Store maximized state before Scene transition, temporarily unmaximize, transition Scene, then re-maximize
   ```java
   boolean wasMaximized = stage.isMaximized();
   if (wasMaximized) {
       stage.setMaximized(false);  // Temporarily restore to normal
   }
   stage.setScene(newScene);  // Transition Scene with valid bounds
   if (wasMaximized) {
       stage.setMaximized(true);   // Re-maximize
   }
   ```

5. **Alternative: Explicit Bounds Preservation**: Manually save and restore bounds during transition
   ```java
   double x = stage.getX();
   double y = stage.getY();
   double width = stage.getWidth();
   double height = stage.getHeight();
   stage.setScene(newScene);
   stage.setX(x);
   stage.setY(y);
   stage.setWidth(width);
   stage.setHeight(height);
   ```

6. **Remove Redundant Maximize Call**: Remove the `stage.setMaximized(true);` call at line 226 in `launchMainApplication()` since the stage should already be maximized from startup
   - This prevents re-triggering the maximize operation during Scene transition
   - Reduces potential for bounds corruption

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the bug on unfixed code (exploratory bug condition checking), then verify the fix works correctly and preserves existing behavior (fix checking and preservation checking).

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the bug BEFORE implementing the fix. Confirm or refute the root cause analysis. If we refute, we will need to re-hypothesize.

**Test Plan**: Write automated tests that launch the application, maximize it, restore it, and check the window Y coordinate. Run these tests on the UNFIXED code to observe failures and confirm the negative Y coordinate issue. Additionally, test Scene transitions while maximized.

**Test Cases**:
1. **Startup Maximize Test**: Launch application, wait for initialization, click restore button, measure window Y coordinate (will be < 0 on unfixed code, confirming Y = -8 or -11 issue)
2. **Scene Transition Test**: Launch application maximized, trigger template button to transition to main view, click restore button, measure window Y coordinate (will be < 0 on unfixed code if Scene transition corrupts bounds)
3. **Title Bar Accessibility Test**: After restoring from maximized, attempt to programmatically access title bar region and verify it's within screen bounds (will fail on unfixed code)
4. **Multiple Restore Cycles Test**: Maximize, restore, maximize again, restore again - verify Y coordinate degrades or stays negative (may progressively worsen on unfixed code)

**Expected Counterexamples**:
- Window Y coordinate is -8 or -11 after restore (negative values)
- Title bar is positioned above Y = 0 (screen top boundary)
- Title bar controls (minimize, maximize, close) are not clickable after restore
- Possible causes: missing centerOnScreen() call, Scene transition while maximized, no explicit bounds preservation

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds (startup maximized or Scene transition while maximized), the fixed function produces the expected behavior (title bar visible with Y ≥ 0).

**Pseudocode:**
```
FOR ALL input WHERE isBugCondition(input) DO
  result := startApplication_fixed(input)  // OR performSceneTransition_fixed(input)
  ASSERT result.windowYCoordinate >= 0
  ASSERT result.titleBarVisible == true
  ASSERT result.titleBarAccessible == true
END FOR
```

**Test Plan**: Run the same test cases as exploratory checking, but on FIXED code. Verify window Y coordinate is ≥ 0, title bar is visible, and controls are accessible.

**Test Cases**:
1. **Fixed Startup Maximize Test**: Launch fixed application, wait for initialization, click restore, verify Y ≥ 0 and title bar visible
2. **Fixed Scene Transition Test**: Launch fixed application, trigger main view transition, click restore, verify Y ≥ 0 and title bar visible
3. **Title Bar Bounds Test**: After restore, verify title bar Y coordinate is between 0 and screen height
4. **Control Accessibility Test**: After restore, programmatically verify minimize/maximize/close buttons are within screen bounds

### Preservation Checking

**Goal**: Verify that for all inputs where the bug condition does NOT hold (manual window operations, operations after valid bounds established), the fixed function produces the same result as the original function.

**Pseudocode:**
```
FOR ALL input WHERE NOT isBugCondition(input) DO
  ASSERT startApplication_original(input).windowBounds = startApplication_fixed(input).windowBounds
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It generates many test cases automatically across the input domain (various window positions, sizes, screen configurations)
- It catches edge cases that manual unit tests might miss (multi-monitor setups, different screen resolutions, various window sizes)
- It provides strong guarantees that behavior is unchanged for all non-buggy inputs

**Test Plan**: Observe behavior on UNFIXED code first for manual window operations (move, resize, minimize/restore), then write property-based tests capturing that behavior. Run tests on both unfixed and fixed code to verify identical behavior.

**Test Cases**:
1. **Manual Window Move Preservation**: Manually move window to position (X, Y) in normal state, verify fixed code preserves exact position (property: for all valid (X, Y), moved window stays at (X, Y))
2. **Manual Window Resize Preservation**: Manually resize window to (W, H) in normal state, verify fixed code preserves exact size (property: for all valid (W, H) above minimum, resized window stays at (W, H))
3. **Multiple Maximize/Restore Preservation**: Start in normal state at position (X, Y), maximize, restore, verify position returns to (X, Y) (property: for all valid starting positions, restore returns to that position)
4. **Minimize/Restore Preservation**: Minimize window then restore, verify position unchanged (property: minimize/restore is identity operation for position)
5. **Subsequent Maximize/Restore Preservation**: After establishing valid bounds, perform multiple maximize/restore cycles, verify position stability (property: after first valid restore, subsequent restores use same position)

### Unit Tests

- Test `ScenoryApplication.start()` with centerOnScreen() called before maximize - verify stage X/Y are positive after initialization
- Test Scene transition in normal (non-maximized) state - verify bounds unchanged
- Test Scene transition after unmaximize-then-remaximize pattern - verify Y ≥ 0 after restore
- Test that font loading, CSS loading, icon loading are unaffected by positioning changes

### Property-Based Tests

- Generate random window positions (X, Y) within valid screen bounds, move window manually, verify position preserved by fixed code
- Generate random window sizes (W, H) above minimum constraints, resize window manually, verify size preserved by fixed code
- Generate random sequences of maximize/restore/move/resize operations starting from normal state, verify all manual operations work identically in fixed code
- Test across multiple screen configurations (single monitor, dual monitor, different resolutions) to verify fix works universally

### Integration Tests

- Test full application launch flow: start maximized → interact with welcome screen → transition to main view → restore window → verify title bar visible and accessible
- Test full window lifecycle: launch maximized → restore → manually move → maximize → restore → verify returns to manual position, not negative offset
- Test template selection flow: launch → click YouTube template → verify main view appears → restore → verify title bar accessible
- Test error cases: launch with very small screen resolution, verify minimum dimensions enforced and title bar still accessible after restore
