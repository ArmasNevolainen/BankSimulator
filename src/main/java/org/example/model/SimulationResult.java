package org.example.model;

/**
 * Represents the results of a single simulation run.
 * Stores identification, timing, and statistical data.
 *
 * @author Group 3
 * @version 1.0
 */
public class SimulationResult {
    /** Unique identifier for the simulation result */
    private int id;
    /** Timestamp when the simulation was completed */
    private String timestamp;
    /** Detailed results and statistics from the simulation run */
    private String results;
    /**
     * Creates a new simulation result with specified parameters.
     *
     * @param id Unique identifier for this result
     * @param timestamp Time when simulation completed
     * @param results Detailed simulation statistics and data
     */
    public SimulationResult(int id, String timestamp, String results) {
        this.id = id;
        this.timestamp = timestamp;
        this.results = results;
    }
    /**
     * Gets the unique identifier of this result.
     *
     * @return The result ID
     */
    public int getId() {
        return id;
    }
    /**
     * Gets the timestamp of when the simulation completed.
     *
     * @return The completion timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }
    /**
     * Gets the detailed simulation results.
     *
     * @return The simulation statistics and data
     */
    public String getResults() {
        return results;
    }
    /**
     * Sets the unique identifier for this result.
     *
     * @param id The new result ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the completion timestamp.
     *
     * @param timestamp The new completion timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * Sets the simulation results data.
     *
     * @param results The new simulation statistics and data
     */
    public void setResults(String results) {
        this.results = results;
    }
}
