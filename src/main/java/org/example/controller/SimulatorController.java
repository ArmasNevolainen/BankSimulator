package org.example.controller;

import org.example.View.SimulatorView;
import org.example.framework.Engine;
import org.example.framework.Trace;
import org.example.model.MyEngine;
import javafx.application.Platform;

public class SimulatorController {
    private SimulatorView view;
    private Engine engine;
    private Thread simulationThread;
    private boolean isPaused = false;
    private double simulationSpeed = 1.0;
    private int numberOfStations = 3;
    private double arrivalInterval = 5.0;

    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    public void startSimulation() {
        if (simulationThread != null && simulationThread.isAlive()) {
            return;
        }

        Trace.setTraceLevel(Trace.Level.INFO);
        engine = new MyEngine();
        engine.setSimulationTime(1000);

        simulationThread = new Thread(() -> {
            try {
                engine.run();
                Platform.runLater(() -> updateStatus("Simulation completed"));
            } catch (Exception e) {
                Platform.runLater(() -> updateStatus("Simulation error: " + e.getMessage()));
            }
        });

        simulationThread.start();
        updateStatus("Simulation started");
    }

    public void pauseSimulation() {
        isPaused = !isPaused;
        updateStatus(isPaused ? "Simulation paused" : "Simulation resumed");
    }

    public void resetSimulation() {
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
        engine = new MyEngine();
        isPaused = false;
        updateStatus("Simulation reset");
    }

    public void setSimulationSpeed(double speed) {
        this.simulationSpeed = speed;
        updateStatus("Speed set to: " + speed);
    }

    public void setNumberOfStations(int stations) {
        this.numberOfStations = stations;
        updateStatus("Number of stations set to: " + stations);
    }

    public void setArrivalInterval(double interval) {
        this.arrivalInterval = interval;
        updateStatus("Arrival interval set to: " + interval);
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> {
            // Update status
        });
    }

    public boolean isPaused() {
        return isPaused;
    }

    public double getSimulationSpeed() {
        return simulationSpeed;
    }

    public int getNumberOfStations() {
        return numberOfStations;
    }

    public double getArrivalInterval() {
        return arrivalInterval;
    }
}
