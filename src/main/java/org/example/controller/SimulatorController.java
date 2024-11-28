package org.example.controller;

import javafx.scene.canvas.GraphicsContext;
import org.example.View.SimulatorView;
import org.example.framework.Engine;
import org.example.framework.Trace;
import org.example.model.Customer;
import org.example.model.CustomerType;
import org.example.model.MyEngine;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulatorController {
    private SimulatorView view;
    private Engine engine;
    private Thread simulationThread;
    private boolean isPaused = false;
    private double simulationSpeed = 1.0;
    private int numberOfStations = 3;
    private double arrivalInterval = 5.0;
    private long sleepTime = 100; // Default sleep time in milliseconds


    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    public void startSimulation() {
        System.out.println("Starting simulation...");

        Trace.setTraceLevel(Trace.Level.INFO);
        engine = new MyEngine(this);
        engine.setQueueUpdateListener(newStatus -> updateQueueStatus(newStatus));
        engine.setSimulationTime(1000);

        simulationThread = new Thread(() -> {
            System.out.println("Simulation thread starting");
            engine.run();
        });

        simulationThread.start();
    }



    public void pauseSimulation() {
        isPaused = !isPaused;
        updateStatus(isPaused ? "Simulation paused" : "Simulation resumed");
    }

    public void resetSimulation() {
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
        engine = new MyEngine(this);
        isPaused = false;
        updateStatus("Simulation reset");
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
    private Map<String, List<Customer>> currentQueueStatus = new HashMap<>();

    public void updateQueueStatus(Map<String, List<Customer>> newStatus) {
        System.out.println("Controller received update: " + newStatus);
        this.currentQueueStatus = newStatus;
        System.out.println("Queue status updated: " + newStatus.size() + " queues");
        Platform.runLater(() -> view.updateQueueVisualization(currentQueueStatus));
    }


    public Map<String, List<Customer>> getQueueStatus() {
        return currentQueueStatus;
    }

    public Map<String, List<Integer>> convertQueueStatusToSigns(Map<String, List<Customer>> queueStatus) {
        return queueStatus.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(customer -> customer.getType() == CustomerType.TRANSACTION_CLIENT ? 1 : 2)
                                .collect(Collectors.toList())
                ));
    }
    public void setSimulationSpeed(double sliderValue) {
        // Convert slider value (0-100) to sleep time (200ms - 0ms)
        this.sleepTime = (long)(200 - (sliderValue * 2));
    }

    public long getSleepTime() {
        return sleepTime;
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
