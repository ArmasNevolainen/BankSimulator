package org.example.database;

import org.example.model.SimulationResult;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DatabaseService {
    private static final String URL = "jdbc:mariadb://localhost:3306/bank_simulation";
    private static final String USER = "appuser";
    private static final String PASSWORD = "password";

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveSimulationResult(SimulationResult result) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO simulation_results (timestamp, results) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, result.getTimestamp());
                stmt.setString(2, result.getResults());
                stmt.executeUpdate();
                System.out.println("Saved results: " + result.getResults());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SimulationResult> getSimulationResults() {
        List<SimulationResult> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM simulation_results ORDER BY timestamp DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    results.add(new SimulationResult(
                            rs.getInt("id"),
                            rs.getString("timestamp"),
                            rs.getString("results")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}