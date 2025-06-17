package com.example.scenory.persistence;

import com.example.scenory.database.DatabaseManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private static final String DEFAULT_USER_ID = "default";
    private static UserPreferences instance;
    private Map<String, String> preferences;

    private UserPreferences() {
        preferences = new HashMap<>();
        loadPreferences();
    }

    public static UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }

    /**
     * Load all preferences from database
     */
    private void loadPreferences() {
        String sql = "SELECT preference_key, preference_value FROM user_preferences WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, DEFAULT_USER_ID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    preferences.put(rs.getString("preference_key"), rs.getString("preference_value"));
                }
            }

            System.out.println("📋 Loaded " + preferences.size() + " user preferences");

        } catch (SQLException e) {
            System.err.println("❌ Error loading preferences: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get preference value with default fallback
     */
    public String get(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }

    /**
     * Get boolean preference
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    /**
     * Get double preference
     */
    public double getDouble(String key, double defaultValue) {
        String value = get(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Set preference and save to database
     */
    public void set(String key, String value) {
        preferences.put(key, value);
        savePreference(key, value);
    }

    /**
     * Set boolean preference
     */
    public void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    /**
     * Set double preference
     */
    public void setDouble(String key, double value) {
        set(key, String.valueOf(value));
    }

    /**
     * Save individual preference to database
     */
    private void savePreference(String key, String value) {
        String sql = """
            INSERT INTO user_preferences (user_id, preference_key, preference_value)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                preference_value = VALUES(preference_value),
                modified_date = CURRENT_TIMESTAMP
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, DEFAULT_USER_ID);
            stmt.setString(2, key);
            stmt.setString(3, value);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error saving preference: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Common preference keys as constants
    public static final String AUTO_SAVE_ENABLED = "auto_save_enabled";
    public static final String AUTO_GENERATE_THUMBNAILS = "auto_generate_thumbnails";
    public static final String DEFAULT_PANEL_DURATION = "default_panel_duration";
    public static final String DEFAULT_CANVAS_BACKGROUND = "default_canvas_background";
    public static final String RECENT_PROJECTS_LIMIT = "recent_projects_limit";
    public static final String TOOL_PANEL_COLLAPSED = "tool_panel_collapsed";
    public static final String SCENE_CONSTRUCTOR_POSITION = "scene_constructor_position";
}