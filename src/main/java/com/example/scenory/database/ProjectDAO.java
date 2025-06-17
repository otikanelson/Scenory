package com.example.scenory.database;

import com.example.scenory.model.Project;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    /**
     * Save a project to the database (insert or update)
     */
    public static void save(Project project) {
        // Check if project has a numeric database ID vs UUID
        if (project.getId() == null || isUUID(project.getId())) {
            insert(project);
        } else {
            update(project);
        }
    }

    /**
     * Check if ID is a UUID (existing projects) vs database ID
     */
    private static boolean isUUID(String id) {
        if (id == null) return false;
        // UUIDs contain hyphens, database IDs don't
        return id.contains("-");
    }

    /**
     * Insert a new project
     */
    private static void insert(Project project) {
        String sql = """
            INSERT INTO projects (name, description, canvas_width, canvas_height, 
                                project_type, aspect_ratio, created_date, modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setInt(3, 800); // Default canvas width
            stmt.setInt(4, 600); // Default canvas height
            stmt.setString(5, "CUSTOM"); // Default project type
            stmt.setString(6, "16:9"); // Default aspect ratio
            stmt.setTimestamp(7, Timestamp.valueOf(project.getCreatedDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(project.getModifiedDate()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Set the database ID (integer) as string
                        project.setId(String.valueOf(generatedKeys.getInt(1)));
                        System.out.println("✅ Project saved with DB ID: " + project.getId());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error saving project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update existing project
     */
    private static void update(Project project) {
        String sql = """
            UPDATE projects 
            SET name = ?, description = ?, modified_date = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, Integer.parseInt(project.getId())); // Safe now - only numeric IDs reach here

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                project.setModifiedDate(LocalDateTime.now());
                System.out.println("✅ Project updated: " + project.getName());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load a project by database ID
     */
    public static Project load(int projectId) {
        String sql = """
            SELECT id, name, description, created_date, modified_date, 
                   file_path, canvas_width, canvas_height, project_type, aspect_ratio
            FROM projects WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Project project = new Project();
                    project.setId(String.valueOf(rs.getInt("id")));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    project.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());
                    project.setFilePath(rs.getString("file_path"));

                    // Load associated scenes (will implement after SceneDAO)
                     project.setScenes(SceneDAO.loadByProjectId(projectId));

                    System.out.println("📂 Loaded project: " + project.getName());
                    return project;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading project: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load project by string ID (handles both UUIDs and database IDs)
     */
    public static Project loadByStringId(String projectId) {
        if (isUUID(projectId)) {
            // This is a UUID - project doesn't exist in database yet
            System.out.println("⚠️ Project with UUID " + projectId + " not in database");
            return null;
        } else {
            // This is a database ID
            return load(Integer.parseInt(projectId));
        }
    }

    /**
     * Load all projects
     */
    public static List<Project> loadAll() {
        List<Project> projects = new ArrayList<>();
        String sql = """
            SELECT id, name, description, created_date, modified_date, 
                   file_path, canvas_width, canvas_height, project_type, aspect_ratio
            FROM projects ORDER BY modified_date DESC
            """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Project project = new Project();
                project.setId(String.valueOf(rs.getInt("id")));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                project.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());
                project.setFilePath(rs.getString("file_path"));

                // Note: Not loading scenes for performance in list view
                projects.add(project);
            }

            System.out.println("📋 Loaded " + projects.size() + " projects");

        } catch (SQLException e) {
            System.err.println("❌ Error loading projects: " + e.getMessage());
            e.printStackTrace();
        }

        return projects;
    }

    /**
     * Delete a project and all associated data
     */
    public static boolean delete(int projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("🗑️ Project deleted (ID: " + projectId + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting project: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}