# Bugfix Requirements Document

## Introduction

This bugfix addresses a critical window positioning issue in the JavaFX application where the window title bar becomes inaccessible when the user restores the window from maximized state on Windows. The bug occurs because the application starts maximized without establishing valid restored bounds, causing Windows to apply negative offset coordinates (Y = -8 or -11, which are normal for borderless maximized windows) to the normal window frame when restoring. Additionally, creating a new Scene object during the transition from welcome screen to main view while the stage is maximized can cause Windows to restore with invalid saved bounds. This makes the window unusable as users cannot access the title bar controls to move, minimize, or close the window.

## Bug Analysis

### Current Behavior (Defect)

1.1 WHEN the application starts maximized (stage.setMaximized(true)) without explicit restored bounds or centerOnScreen() call THEN the system saves negative window offset coordinates (Y = -8 or Y = -11) as restored bounds

1.2 WHEN the user clicks the restore/unmaximize button to exit maximized state THEN the system applies the negative offset coordinates to the normal window frame, pushing the title bar above the visible screen area

1.3 WHEN a new Scene object is created and set on the stage during the transition from welcome screen to main view while the stage is still maximized THEN the system may cause Windows to restore the window with invalid saved bounds

1.4 WHEN the window title bar is positioned above the screen viewport THEN the system renders the window controls (minimize, maximize, close, move) inaccessible to the user

### Expected Behavior (Correct)

2.1 WHEN the application starts THEN the system SHALL establish explicit restored bounds with valid positive coordinates before setting the stage to maximized

2.2 WHEN the user clicks the restore/unmaximize button to exit maximized state THEN the system SHALL restore the window with the title bar fully visible within the screen viewport with all controls accessible

2.3 WHEN transitioning from welcome screen to main view THEN the system SHALL preserve valid window bounds across Scene transitions or re-establish valid bounds before/after the transition

2.4 WHEN the stage is set to maximized THEN the system SHALL ensure that centerOnScreen() is called or explicit X/Y coordinates are set to valid positive values for restored state

### Unchanged Behavior (Regression Prevention)

3.1 WHEN the application starts in maximized state THEN the system SHALL CONTINUE TO display the window maximized correctly with no title bar visible issues in maximized mode

3.2 WHEN the user manually moves or resizes the window in normal (non-maximized) state THEN the system SHALL CONTINUE TO preserve those position and size values correctly

3.3 WHEN the user toggles between maximized and restored states multiple times THEN the system SHALL CONTINUE TO remember the last valid restored position and size

3.4 WHEN loading custom fonts, stylesheets, and application icons during startup THEN the system SHALL CONTINUE TO function correctly without interference from window positioning fixes

3.5 WHEN setting minimum window dimensions (setMinWidth/setMinHeight) THEN the system SHALL CONTINUE TO enforce those constraints correctly
