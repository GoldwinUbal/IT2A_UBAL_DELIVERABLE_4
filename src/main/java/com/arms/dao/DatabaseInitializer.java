package com.arms.dao;

import com.arms.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                user_id  INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role     TEXT NOT NULL CHECK (role IN ('ADMINISTRATOR','PROPERTY_MANAGER','TENANT')),
                status   TEXT NOT NULL DEFAULT 'PENDING'
            );
            """;

    private static final String CREATE_UNITS = """
            CREATE TABLE IF NOT EXISTS units (
                unit_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                unit_number  TEXT NOT NULL UNIQUE,
                unit_type    TEXT NOT NULL,
                monthly_rent REAL NOT NULL,
                status       TEXT NOT NULL DEFAULT 'AVAILABLE'
            );
            """;

    private static final String CREATE_TENANTS = """
            CREATE TABLE IF NOT EXISTS tenants (
                tenant_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id   INTEGER NOT NULL UNIQUE,
                name      TEXT NOT NULL,
                contact   TEXT,
                unit_id   INTEGER,
                FOREIGN KEY (user_id) REFERENCES users(user_id),
                FOREIGN KEY (unit_id) REFERENCES units(unit_id)
            );
            """;

    private static final String CREATE_PAYMENTS = """
            CREATE TABLE IF NOT EXISTS payments (
                payment_id     INTEGER PRIMARY KEY AUTOINCREMENT,
                tenant_id      INTEGER NOT NULL,
                unit_id        INTEGER NOT NULL,
                amount_paid    REAL NOT NULL,
                payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                payment_status TEXT NOT NULL,
                receipt_number TEXT,
                FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id),
                FOREIGN KEY (unit_id)   REFERENCES units(unit_id)
            );
            """;

    private static final String CREATE_ACTIVITY_LOGS = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                log_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id     INTEGER NOT NULL,
                action      TEXT NOT NULL,
                description TEXT,
                timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            );
            """;

    private static final String SEED_ADMIN = """
            INSERT OR IGNORE INTO users (username, password, role, status)
            VALUES (?, ?, 'ADMINISTRATOR', 'ACTIVE');
            """;

    public void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(CREATE_USERS);
            stmt.execute(CREATE_UNITS);
            stmt.execute(CREATE_TENANTS);
            stmt.execute(CREATE_PAYMENTS);
            stmt.execute(CREATE_ACTIVITY_LOGS);

            try (PreparedStatement ps = conn.prepareStatement(SEED_ADMIN)) {
                ps.setString(1, "admin");
                ps.setString(2, PasswordUtil.hash("admin123"));
                ps.executeUpdate();
            }

            System.out.println("[OK] Database initialized.");
        } catch (SQLException e) {
            System.err.println("[ERROR] Database initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
