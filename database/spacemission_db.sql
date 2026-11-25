CREATE DATABASE IF NOT EXISTS spacemission_db;
USE spacemission_db;

CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(120) UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS spacecraft_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS spacecraft (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    serial_number VARCHAR(80) NOT NULL UNIQUE,
    spacecraft_type_id INT NOT NULL,
    manufacturer VARCHAR(120),
    launch_date DATE,
    status VARCHAR(30) NOT NULL,
    fuel_level DECIMAL(10,2) NOT NULL DEFAULT 100.00,
    battery_level DECIMAL(10,2) NOT NULL DEFAULT 100.00,
    altitude DECIMAL(10,2) DEFAULT 0,
    velocity DECIMAL(10,2) DEFAULT 0,
    orbit_type VARCHAR(50),
    temperature DECIMAL(10,2) DEFAULT 0,
    communication_status VARCHAR(30) DEFAULT 'ONLINE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_spacecraft_type FOREIGN KEY (spacecraft_type_id) REFERENCES spacecraft_types(id)
);

CREATE TABLE IF NOT EXISTS missions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mission_code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    mission_type VARCHAR(50) NOT NULL,
    description TEXT,
    priority VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    start_date DATETIME,
    expected_end_date DATETIME,
    actual_end_date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mission_spacecraft (
    mission_id INT NOT NULL,
    spacecraft_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (mission_id, spacecraft_id),
    CONSTRAINT fk_ms_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT fk_ms_spacecraft FOREIGN KEY (spacecraft_id) REFERENCES spacecraft(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mission_team (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mission_id INT NOT NULL,
    user_id INT NOT NULL,
    role_name VARCHAR(80),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_team_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT fk_team_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS telemetry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    spacecraft_id INT NOT NULL,
    temperature DECIMAL(10,2),
    voltage DECIMAL(10,2),
    battery_percentage DECIMAL(10,2),
    fuel_percentage DECIMAL(10,2),
    altitude DECIMAL(10,2),
    velocity DECIMAL(10,2),
    pressure DECIMAL(10,2),
    signal_strength DECIMAL(10,2),
    solar_panel_efficiency DECIMAL(10,2),
    communication_quality DECIMAL(10,2),
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telemetry_spacecraft FOREIGN KEY (spacecraft_id) REFERENCES spacecraft(id) ON DELETE CASCADE,
    INDEX idx_telemetry_spacecraft_time (spacecraft_id, captured_at)
);

CREATE TABLE IF NOT EXISTS anomaly_rules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    threshold_value DECIMAL(10,2),
    metric_name VARCHAR(80),
    description TEXT
);

CREATE TABLE IF NOT EXISTS anomalies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    spacecraft_id INT,
    mission_id INT,
    rule_id INT,
    severity VARCHAR(30) NOT NULL,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    status VARCHAR(30) DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_anomaly_spacecraft FOREIGN KEY (spacecraft_id) REFERENCES spacecraft(id) ON DELETE SET NULL,
    CONSTRAINT fk_anomaly_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE SET NULL,
    CONSTRAINT fk_anomaly_rule FOREIGN KEY (rule_id) REFERENCES anomaly_rules(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS resources (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    type VARCHAR(80) NOT NULL,
    total_quantity DECIMAL(10,2) NOT NULL,
    available_quantity DECIMAL(10,2) NOT NULL,
    unit VARCHAR(30),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mission_resources (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mission_id INT NOT NULL,
    resource_id INT NOT NULL,
    allocated_quantity DECIMAL(10,2) NOT NULL,
    consumed_quantity DECIMAL(10,2) DEFAULT 0,
    allocated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mission_resources_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT fk_mission_resources_resource FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS communication_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    spacecraft_id INT,
    mission_id INT,
    sender VARCHAR(120) NOT NULL,
    recipient VARCHAR(120),
    command_type VARCHAR(80) NOT NULL,
    payload TEXT,
    status VARCHAR(30) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comm_log_spacecraft FOREIGN KEY (spacecraft_id) REFERENCES spacecraft(id) ON DELETE SET NULL,
    CONSTRAINT fk_comm_log_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS spacecraft_commands (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    spacecraft_id INT NOT NULL,
    command_type VARCHAR(80) NOT NULL,
    status VARCHAR(30) NOT NULL,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP NULL,
    details TEXT,
    CONSTRAINT fk_command_spacecraft FOREIGN KEY (spacecraft_id) REFERENCES spacecraft(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mission_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mission_id INT NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    description TEXT,
    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level VARCHAR(20) NOT NULL,
    source VARCHAR(120),
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description) VALUES
('ADMIN', 'Full system privileges'),
('MISSION_CONTROL_OPERATOR', 'Mission monitoring and command handling'),
('ENGINEER', 'Technical system monitoring and maintenance'),
('SCIENTIST', 'Mission analysis and scientific oversight');

INSERT INTO users (username, password_hash, email, full_name, enabled) VALUES
('admin', '$2a$12$6QnB9E2Qw8r0N3hjQJ8M7uF9D7mLw6Wb7OTnU6q8h5nQ9wLk7kI0K', 'admin@spacemission.local', 'System Administrator', TRUE),
('operator', '$2a$12$6QnB9E2Qw8r0N3hjQJ8M7uF9D7mLw6Wb7OTnU6q8h5nQ9wLk7kI0K', 'operator@spacemission.local', 'Mission Operator', TRUE),
('engineer', '$2a$12$6QnB9E2Qw8r0N3hjQJ8M7uF9D7mLw6Wb7OTnU6q8h5nQ9wLk7kI0K', 'engineer@spacemission.local', 'Systems Engineer', TRUE),
('scientist', '$2a$12$6QnB9E2Qw8r0N3hjQJ8M7uF9D7mLw6Wb7OTnU6q8h5nQ9wLk7kI0K', 'scientist@spacemission.local', 'Lead Scientist', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ADMIN' WHERE u.username = 'admin';

INSERT INTO spacecraft_types (name, description) VALUES
('COMMUNICATION_SATELLITE', 'Relays data and maintains communication links'),
('OBSERVATION_SATELLITE', 'Captures earth and space observation data'),
('NAVIGATION_SATELLITE', 'Provides orbital navigation support'),
('SCIENTIFIC_SATELLITE', 'Research and scientific survey missions'),
('EXPLORATION_SPACECRAFT', 'Deep-space or exploratory vehicle');

INSERT INTO anomaly_rules (name, severity, enabled, threshold_value, metric_name, description) VALUES
('LOW_BATTERY', 'WARNING', TRUE, 20.0, 'battery_percentage', 'Battery below warning threshold'),
('CRITICAL_FUEL', 'CRITICAL', TRUE, 15.0, 'fuel_percentage', 'Fuel below critical threshold'),
('HIGH_TEMPERATURE', 'WARNING', TRUE, 70.0, 'temperature', 'Temperature above safe range'),
('COMMUNICATION_LOSS', 'CRITICAL', TRUE, 10.0, 'signal_strength', 'Communication signal too weak');
