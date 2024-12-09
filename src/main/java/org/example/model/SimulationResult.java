package org.example.model;

public class SimulationResult {
    private int id;
    private String timestamp;
    private String results;

    public SimulationResult(int id, String timestamp, String results) {
        this.id = id;
        this.timestamp = timestamp;
        this.results = results;
    }
    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getResults() {
        return results;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
