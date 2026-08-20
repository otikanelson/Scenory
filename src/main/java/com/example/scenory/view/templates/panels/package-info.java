/**
 * Template classes for reusable interface panels.
 * <p>
 * This package provides template classes for creating consistent interface panels throughout
 * the Scenory application. Each template provides standardized layout, styling, and behavior
 * while supporting customization through configuration objects.
 * </p>
 * 
 * <h2>Panel Templates:</h2>
 * <ul>
 *   <li>{@code ToolsPanelTemplate} - Vertical panel for tool buttons with selection management</li>
 *   <li>{@code ScenePanelTemplate} - Panel for frame thumbnails and layer information</li>
 *   <li>{@code TimelinePanelTemplate} - Horizontal timeline for frame navigation</li>
 *   <li>{@code StatusPanelTemplate} - Status bar for zoom, FPS, and frame count display</li>
 * </ul>
 * 
 * <h2>Usage Pattern:</h2>
 * <pre>{@code
 * ToolsPanelConfig config = ToolsPanelConfig.builder()
 *     .toolSpacing(10)
 *     .showLabels(true)
 *     .build();
 * 
 * ToolsPanelTemplate toolsPanel = new ToolsPanelTemplate(config);
 * toolsPanel.addTool("pencil", icon, "Pencil", this::onToolSelect);
 * }</pre>
 * 
 * @since 1.0
 */
package com.example.scenory.view.templates.panels;
