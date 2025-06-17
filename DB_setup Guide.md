# Scenory Database Setup Guide
## Complete MySQL Database Configuration with PopSQL

### 📋 **Prerequisites**

Before setting up the Scenory database, ensure you have:

1. **MySQL Server** (8.0 or higher)
    - [Download MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
    - Or use [XAMPP](https://www.apachefriends.org/) for easy local setup
2. **PopSQL** (Free database client)
    - [Download PopSQL](https://popsql.com/)
3. **Java Project** with MySQL Connector dependency

---

## 🚀 **Step 1: Install MySQL Server**

### **Option A: MySQL Community Server (Recommended)**

1. **Download and Install:**
    - Go to [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)
    - Choose your operating system
    - Download and run the installer

2. **During Installation:**
    - Choose "Developer Default" setup type
    - Set root password (remember this!)
    - Default port: `3306`
    - Character set: `utf8mb4`

3. **Verify Installation:**
   ```bash
   mysql --version
   ```

### **Option B: XAMPP (Easier for Beginners)**

1. **Download XAMPP:**
    - Go to [XAMPP Download](https://www.apachefriends.org/)
    - Install XAMPP with MySQL component

2. **Start MySQL:**
    - Open XAMPP Control Panel
    - Click "Start" next to MySQL
    - Default credentials: username `root`, no password

---

## 🗄️ **Step 2: Install and Configure PopSQL**

1. **Download PopSQL:**
    - Visit [PopSQL.com](https://popsql.com/)
    - Download for your operating system
    - Install the application

2. **Create New Connection:**
    - Open PopSQL
    - Click "New Connection"
    - Choose "MySQL"

3. **Connection Settings:**
   ```
   Host: localhost (or 127.0.0.1)
   Port: 3306
   Database: (leave empty for now)
   Username: root
   Password: [your MySQL root password]
   ```

4. **Test Connection:**
    - Click "Test Connection"
    - Should show "Connection successful"
    - Click "Connect"

---

## 🏗️ **Step 3: Create Scenory Database**

### **Execute Database Creation Script**

Copy and paste this script into PopSQL and execute:

```sql
-- =====================================================
-- SCENORY DATABASE CREATION SCRIPT
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS scenory_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE scenory_db;

-- =====================================================
-- CORE PROJECT TABLES
-- =====================================================

-- Projects table
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    file_path VARCHAR(500),
    thumbnail_path VARCHAR(500),
    
    -- Project settings
    canvas_width INT DEFAULT 800,
    canvas_height INT DEFAULT 600,
    project_type ENUM('YOUTUBE', 'FILM', 'CUSTOM') DEFAULT 'CUSTOM',
    aspect_ratio VARCHAR(10) DEFAULT '16:9',
    
    INDEX idx_name (name),
    INDEX idx_created_date (created_date)
) ENGINE=InnoDB;

-- Scenes table
CREATE TABLE scenes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    background_color VARCHAR(7) DEFAULT '#FFFFFF',
    estimated_duration_seconds INT DEFAULT 0,
    sequence_order INT DEFAULT 0,
    
    -- Scene metadata
    location VARCHAR(255),
    time_of_day VARCHAR(100),
    is_completed BOOLEAN DEFAULT FALSE,
    notes TEXT,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_project_sequence (project_id, sequence_order),
    INDEX idx_project_id (project_id)
) ENGINE=InnoDB;

-- Panels table (Enhanced for Phase 1)
CREATE TABLE panels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    scene_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    
    -- Rich text descriptions (Phase 1)
    description_rich_text LONGTEXT,
    description_plain_text TEXT,
    
    -- Panel visual settings (Phase 1)
    canvas_background_color VARCHAR(7) DEFAULT '#FFFFFF',
    canvas_width INT DEFAULT 800,
    canvas_height INT DEFAULT 600,
    
    -- Video export settings (Phase 1)
    display_duration_seconds DECIMAL(5,2) DEFAULT 3.0,
    
    -- Panel content
    sequence_order INT DEFAULT 0,
    canvas_data LONGTEXT,
    thumbnail_data LONGBLOB,
    
    -- Panel metadata
    shot_type VARCHAR(100),
    camera_angle VARCHAR(100),
    dialogue TEXT,
    action TEXT,
    camera_movement VARCHAR(100),
    is_key_frame BOOLEAN DEFAULT FALSE,
    transition_type VARCHAR(50),
    audio_notes TEXT,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (scene_id) REFERENCES scenes(id) ON DELETE CASCADE,
    INDEX idx_scene_sequence (scene_id, sequence_order),
    INDEX idx_scene_id (scene_id)
) ENGINE=InnoDB;

-- =====================================================
-- USER INTERFACE CONFIGURATION TABLES
-- =====================================================

-- Panel layout configurations (Phase 1)
CREATE TABLE panel_layouts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) DEFAULT 'default',
    layout_name VARCHAR(100) DEFAULT 'default',
    
    -- Left panel settings
    left_panel_collapsed BOOLEAN DEFAULT FALSE,
    tool_panel_collapsed BOOLEAN DEFAULT TRUE,
    file_structure_collapsed BOOLEAN DEFAULT FALSE,
    left_panel_width DOUBLE DEFAULT 250.0,
    
    -- Scene constructor settings
    scene_constructor_position ENUM('TOP', 'RIGHT', 'BOTTOM') DEFAULT 'RIGHT',
    scene_constructor_visible BOOLEAN DEFAULT TRUE,
    right_panel_width DOUBLE DEFAULT 300.0,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_user_layout (user_id, layout_name)
) ENGINE=InnoDB;

-- User preferences
CREATE TABLE user_preferences (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) DEFAULT 'default',
    preference_key VARCHAR(100) NOT NULL,
    preference_value TEXT,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_user_preference (user_id, preference_key)
) ENGINE=InnoDB;

-- =====================================================
-- ASSET MANAGEMENT TABLES (Future Phases)
-- =====================================================

-- Assets library
CREATE TABLE assets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    file_path VARCHAR(500),
    thumbnail_path VARCHAR(500),
    file_size_bytes BIGINT,
    file_type VARCHAR(50),
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB;

-- Drawing elements (for future vector support)
CREATE TABLE drawing_elements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    panel_id INT NOT NULL,
    element_type VARCHAR(50) NOT NULL, -- 'path', 'shape', 'text', etc.
    element_data LONGTEXT, -- JSON or serialized drawing data
    
    -- Position and styling
    x_position DOUBLE DEFAULT 0,
    y_position DOUBLE DEFAULT 0,
    width DOUBLE DEFAULT 0,
    height DOUBLE DEFAULT 0,
    color VARCHAR(7),
    stroke_width DOUBLE DEFAULT 1.0,
    
    layer_order INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (panel_id) REFERENCES panels(id) ON DELETE CASCADE,
    INDEX idx_panel_layer (panel_id, layer_order)
) ENGINE=InnoDB;

-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Insert default layout
INSERT INTO panel_layouts (user_id, layout_name) VALUES ('default', 'default');

-- Insert default preferences
INSERT INTO user_preferences (user_id, preference_key, preference_value) VALUES
('default', 'auto_save_enabled', 'true'),
('default', 'auto_generate_thumbnails', 'true'),
('default', 'default_panel_duration', '3.0'),
('default', 'default_canvas_background', '#FFFFFF'),
('default', 'recent_projects_limit', '5');

-- Create sample project for testing
INSERT INTO projects (name, description, project_type) VALUES
('Sample YouTube Project', 'A sample project for testing purposes', 'YOUTUBE');

-- Get the project ID (will be 1 if this is first project)
SET @project_id = LAST_INSERT_ID();

-- Create sample scene
INSERT INTO scenes (project_id, title, description, sequence_order) VALUES
(@project_id, 'Opening Scene', 'Introduction scene for the video', 0);

-- Get the scene ID
SET @scene_id = LAST_INSERT_ID();

-- Create sample panels
INSERT INTO panels (scene_id, title, sequence_order, display_duration_seconds) VALUES
(@scene_id, 'Panel 1 - Title Card', 0, 2.5),
(@scene_id, 'Panel 2 - Character Introduction', 1, 4.0),
(@scene_id, 'Panel 3 - Main Action', 2, 3.5);
```

---

## ✅ **Step 4: Verify Database Setup**

### **Check Tables Creation**

Execute these verification queries in PopSQL:

```sql
-- Check if all tables were created
SHOW TABLES;

-- Verify projects table structure
DESCRIBE projects;

-- Verify panels table with new Phase 1 columns
DESCRIBE panels;

-- Check sample data
SELECT p.name, s.title, COUNT(pan.id) as panel_count
FROM projects p
LEFT JOIN scenes s ON p.id = s.project_id
LEFT JOIN panels pan ON s.id = pan.scene_id
GROUP BY p.id, s.id;

-- Check panel layouts table
SELECT * FROM panel_layouts;

-- Check user preferences
SELECT * FROM user_preferences;
```

**Expected Results:**
- 8 tables should be created
- Sample project with 1 scene and 3 panels should exist
- Default layout and preferences should be inserted

---

## 🔗 **Step 5: Configure Java Database Connection**

### **Update Maven Dependencies**

Add to your `pom.xml` if not already added :

```xml
<dependencies>
    <!-- Existing JavaFX dependencies -->
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- HikariCP for connection pooling -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.0.1</version>
    </dependency>
    
    <!-- Jackson for JSON handling -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
```

### **Create Database Configuration Files**

#### **🔒 Method 1: Environment Variables (Recommended for GitHub)**

Create `src/main/resources/database.properties` (safe for GitHub):

```properties
# Scenory Database Configuration
# This file is safe to commit to GitHub - no passwords here!

# Database Connection
db.host=${DB_HOST:localhost}
db.port=${DB_PORT:3306}
db.name=${DB_NAME:scenory_db}
db.username=${DB_USERNAME:root}
db.password=${DB_PASSWORD:}

# Connection Pool Settings
db.pool.maximum=10
db.pool.minimum=2
db.pool.timeout=30000

# Application Settings
app.auto.save=true
app.thumbnail.generation=true
app.default.panel.duration=3.0
```

### **Create Enhanced Database Manager Class**

Create `src/main/java/com/example/scenory/database/DatabaseManager.java`:

```java
package com.example.scenory.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    
    private DatabaseManager() {
        initializeDataSource();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                    props.getProperty("db.host"),
                    props.getProperty("db.port"),
                    props.getProperty("db.name")));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximum", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimum", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.timeout", "30000")));
            
            this.dataSource = new HikariDataSource(config);
            
            System.out.println("✅ Database connection pool initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        
        // Try to load local config first (with actual passwords)
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database-local.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("📁 Loaded local database configuration");
                return props;
            }
        } catch (IOException e) {
            System.out.println("ℹ️ No local config found, trying default config...");
        }
        
        // Fallback to main config (with environment variables)
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("database.properties file not found");
            }
            props.load(input);
            
            // Resolve environment variables
            props = resolveEnvironmentVariables(props);
            System.out.println("📁 Loaded database configuration with environment variables");
            
        }
        return props;
    }
    
    private Properties resolveEnvironmentVariables(Properties props) {
        Properties resolved = new Properties();
        
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            
            // Check for environment variable syntax: ${VAR_NAME:default_value}
            if (value.startsWith("${") && value.endsWith("}")) {
                String envVar = value.substring(2, value.length() - 1);
                String defaultValue = "";
                
                if (envVar.contains(":")) {
                    String[] parts = envVar.split(":", 2);
                    envVar = parts[0];
                    defaultValue = parts[1];
                }
                
                String envValue = System.getenv(envVar);
                resolved.setProperty(key, envValue != null ? envValue : defaultValue);
            } else {
                resolved.setProperty(key, value);
            }
        }
        
        return resolved;
    }
    
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }
    
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("🔒 Database connection pool closed");
        }
    }
    
    // Test connection method
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
```

---

## 🧪 **Step 6: Test Database Connection**

### **Create Test Class**

Create `src/main/java/com/example/scenory/database/DatabaseTest.java`:

```java
package com.example.scenory.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseTest {
    public static void main(String[] args) {
        testDatabaseConnection();
    }

    public static void testDatabaseConnection() {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        System.out.println("🧪 Testing database connection...");

        if (!dbManager.testConnection()) {
            System.err.println("❌ Database connection failed!");
            return;
        }

        try (Connection conn = dbManager.getConnection()) {
            // Test basic query
            String sql = "SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_schema = 'scenory_db'";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    int tableCount = rs.getInt("table_count");
                    System.out.println("✅ Database connected successfully!");
                    System.out.println("📊 Found " + tableCount + " tables in scenory_db");
                }
            }

            // Test sample data
            sql = "SELECT p.name, COUNT(s.id) as scene_count FROM projects p LEFT JOIN scenes s ON p.id = s.project_id GROUP BY p.id";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("\n📋 Projects in database:");
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("name") + " (" + rs.getInt("scene_count") + " scenes)");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n🎉 Database setup complete and working!");
    }
}
```

---

## 📋 **Step 7: Update Module Configuration**

Update `src/main/java/module-info.java`:

```java
module com.example.scenory {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.web; // For rich text editor

    // Database requirements
    requires java.sql;
    requires com.zaxxer.hikari;
    requires com.fasterxml.jackson.databind;

    opens com.example.scenory to javafx.fxml;
    opens com.example.scenory.controller to javafx.fxml;
    opens com.example.scenory.model to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.example.scenory.database to com.fasterxml.jackson.databind;

    exports com.example.scenory;
    exports com.example.scenory.model;
    exports com.example.scenory.database;
}
```

---

## 🚨 **Troubleshooting Common Issues**

### **Connection Refused Error**
```
Solution: Ensure MySQL server is running
- Check XAMPP Control Panel (if using XAMPP)
- Or restart MySQL service: sudo service mysql restart
```

### **Access Denied Error**
```
Solution: Check username/password in database.properties
- Default XAMPP: username=root, password=empty
- Standard MySQL: Use the password you set during installation
```

### **Database Not Found Error**
```
Solution: Re-run the database creation script
- Execute the entire SQL script in PopSQL
- Verify database creation: SHOW DATABASES;
```

### **Port Already in Use**
```
Solution: Change MySQL port or stop conflicting service
- Default port 3306 might be used by another service
- Change port in MySQL config and database.properties
```

---

## 📁 **Project File Structure**

After setup, your project should look like:

```
scenory/
├── src/main/java/
│   └── com/example/scenory/
│       ├── database/
│       │   ├── DatabaseManager.java
│       │   └── DatabaseTest.java
│       ├── model/
│       ├── controller/
│       └── ScenoryApplication.java
├── src/main/resources/
│   ├── database.properties
│   └── com/example/scenory/
│       ├── styles.css
│       └── *.fxml files
├── pom.xml
└── README.md
```

---

## ✅ **Final Verification Checklist**

- [ ] MySQL Server is installed and running
- [ ] PopSQL connects to MySQL successfully
- [ ] Scenory database exists with all 8 tables
- [ ] Sample data is inserted correctly
- [ ] Java project has MySQL dependencies
- [ ] `database.properties` file is configured
- [ ] `DatabaseManager.java` successfully connects
- [ ] `DatabaseTest.java` runs without errors
- [ ] Module configuration includes database modules

---

## 🔄 **Next Steps**

Once your database is set up:

1. **Test the connection** by running `DatabaseTest.java`
2. **Integrate with existing models** by adding database persistence
3. **Begin Phase 1 implementation** with the panel system
4. **Set up automatic backups** for your development database

Your Scenory database is now ready for development! 🎉