package com.arms.dao;

import com.arms.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.role());
            ps.setString(4, user.status());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return false;
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_id, username, password, role, status FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> findByStatus(String status) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, status FROM users WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return users;
    }

    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("status"));
    }
}
