package com.example.scenory.database;

import java.sql.*;
import java.time.LocalDateTime;

public class PanelLayoutDAO {

    /**
     * Panel layout data class
     */
    public static class PanelLayout {
        private boolean leftPanelCollapsed;
        private boolean toolPanelCollapsed;
        private boolean fileStructureCollapsed;
        private String sceneConstructorPosition; // "TOP", "RIGHT", "BOTTOM"
        private boolean sceneConstructorVisible;
        private double leftPanelWidth;
        private double rightPanelWidth;

        // Default Clip Studio layout
        public PanelLayout() {
            this.leftPanelCollapsed = false;
            this.toolPanelCollapsed = true;  // Start collapsed (icons only)
            this.fileStructureCollapsed = false;
            this.sceneConstructorPosition = "RIGHT";
            this.sceneConstructorVisible = true;
            this.leftPanelWidth = 250.0;
            this.rightPanelWidth = 300.0;
        }

        // Getters and setters
        public boolean isLeftPanelCollapsed() { return leftPanelCollapsed; }
        public void setLeftPanelCollapsed(boolean leftPanelCollapsed) { this.leftPanelCollapsed = leftPanelCollapsed; }

        public boolean isToolPanelCollapsed() { return toolPanelCollapsed; }
        public void setToolPanelCollapsed(boolean toolPanelCollapsed) { this.toolPanelCollapsed = toolPanelCollapsed; }

        public boolean isFileStructureCollapsed() { return fileStructureCollapsed; }
        public void setFileStructureCollapsed(boolean fileStructureCollapsed) { this.fileStructureCollapsed = fileStructureCollapsed; }

        public String getSceneConstructorPosition() { return sceneConstructorPosition; }
        public void setSceneConstructorPosition(String sceneConstructorPosition) { this.sceneConstructorPosition = sceneConstructorPosition; }

        public boolean isSceneConstructorVisible() { return sceneConstructorVisible; }
        public void setSceneConstructorVisible(boolean sceneConstructorVisible) { this.sceneConstructorVisible = sceneConstructorVisible; }

        public double getLeftPanelWidth() { return leftPanelWidth; }
        public void setLeftPanelWidth(double leftPanelWidth) { this.leftPanelWidth = leftPanelWidth; }

        public double getRightPanelWidth() { return rightPanelWidth; }
        public void setRightPanelWidth(double rightPanelWidth) { this.rightPanelWidth = rightPanelWidth; }
    }

    /**
     * Save panel layout for a user
     */
    public static void saveLayout(String userId, String layoutName, PanelLayout layout) {
        String sql = """
            INSERT INTO panel_layouts (user_id, layout_name, left_panel_collapsed, 
                                     tool_panel_collapsed, file_structure_collapsed,
                                     scene_constructor_position, scene_constructor_visible,
                                     left_panel_width, right_panel_width, modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                left_panel_collapsed = VALUES(left_panel_collapsed),
                tool_panel_collapsed = VALUES(tool_panel_collapsed),
                file_structure_collapsed = VALUES(file_structure_collapsed),
                scene_constructor_position = VALUES(scene_constructor_position),
                scene_constructor_visible = VALUES(scene_constructor_visible),
                left_panel_width = VALUES(left_panel_width),
                right_panel_width = VALUES(right_panel_width),
                modified_date = VALUES(modified_date)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);
            stmt.setBoolean(3, layout.isLeftPanelCollapsed());
            stmt.setBoolean(4, layout.isToolPanelCollapsed());
            stmt.setBoolean(5, layout.isFileStructureCollapsed());
            stmt.setString(6, layout.getSceneConstructorPosition());
            stmt.setBoolean(7, layout.isSceneConstructorVisible());
            stmt.setDouble(8, layout.getLeftPanelWidth());
            stmt.setDouble(9, layout.getRightPanelWidth());
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Panel layout saved for user: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saving panel layout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load panel layout for a user
     */
    public static PanelLayout loadLayout(String userId, String layoutName) {
        String sql = """
            SELECT left_panel_collapsed, tool_panel_collapsed, file_structure_collapsed,
                   scene_constructor_position, scene_constructor_visible,
                   left_panel_width, right_panel_width
            FROM panel_layouts 
            WHERE user_id = ? AND layout_name = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PanelLayout layout = new PanelLayout();
                    layout.setLeftPanelCollapsed(rs.getBoolean("left_panel_collapsed"));
                    layout.setToolPanelCollapsed(rs.getBoolean("tool_panel_collapsed"));
                    layout.setFileStructureCollapsed(rs.getBoolean("file_structure_collapsed"));
                    layout.setSceneConstructorPosition(rs.getString("scene_constructor_position"));
                    layout.setSceneConstructorVisible(rs.getBoolean("scene_constructor_visible"));
                    layout.setLeftPanelWidth(rs.getDouble("left_panel_width"));
                    layout.setRightPanelWidth(rs.getDouble("right_panel_width"));

                    System.out.println("📐 Loaded panel layout for user: " + userId);
                    return layout;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading panel layout: " + e.getMessage());
            e.printStackTrace();
        }

        // Return default layout if not found
        System.out.println("📐 Using default panel layout for user: " + userId);
        return new PanelLayout();
    }

    /**
     * Delete a panel layout
     */
    public static boolean deleteLayout(String userId, String layoutName) {
        String sql = "DELETE FROM panel_layouts WHERE user_id = ? AND layout_name = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, layoutName);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("🗑️ Panel layout deleted for user: " + userId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting panel layout: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}