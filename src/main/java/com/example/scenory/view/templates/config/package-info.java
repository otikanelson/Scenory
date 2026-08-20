/**
 * Configuration objects for customizing template components.
 * <p>
 * This package provides configuration classes that act as builders for template components,
 * allowing developers to customize template behavior, styling, and dimensions without
 * modifying template code directly.
 * </p>
 * 
 * <h2>Configuration Classes:</h2>
 * <ul>
 *   <li>{@code ModalConfig} - Configuration for modal dialog templates</li>
 *   <li>{@code PanelConfig} - Base configuration for panel templates</li>
 *   <li>{@code ToolsPanelConfig} - Configuration for tools panel templates</li>
 *   <li>{@code ScenePanelConfig} - Configuration for scene panel templates</li>
 *   <li>{@code TimelinePanelConfig} - Configuration for timeline panel templates</li>
 *   <li>{@code StatusPanelConfig} - Configuration for status bar templates</li>
 * </ul>
 * 
 * <h2>Usage Pattern:</h2>
 * <pre>{@code
 * ModalConfig config = ModalConfig.builder()
 *     .title("My Dialog")
 *     .dimensions(800, 600)
 *     .resizable(true)
 *     .build();
 * 
 * ModalTemplate modal = ModalTemplate.create(config);
 * modal.showAndWait();
 * }</pre>
 * 
 * @since 1.0
 */
package com.example.scenory.view.templates.config;
