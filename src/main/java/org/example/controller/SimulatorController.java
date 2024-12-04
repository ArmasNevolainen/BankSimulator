package org.example.controller;

import javafx.scene.canvas.GraphicsContext;
import org.example.View.SimulatorView;
import org.example.framework.Clock;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimulatorController {
    private SimulatorView view;
    private MyEngine engine;
    private Thread simulationThread;
    private boolean isPaused = false;
    private double simulationSpeed = 1.0;
    private int numberOfStations = 3;
    private double arrivalInterval = 5.0;
    private long sleepTime = 100;// Default sleep time in milliseconds
    private double simulationTime = 1000.0;


    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    public void startSimulation() {
        System.gc();
        System.out.println("Starting simulation...");

        // Reset the clock to 0
        Clock.getInstance().reset();

        // Create fresh engine instance
        Trace.setTraceLevel(Trace.Level.INFO);
        engine = new MyEngine(this);
        engine.setQueueUpdateListener(newStatus -> updateQueueStatus(newStatus));
        engine.setSimulationTime(1000);

        // Start new simulation thread
        simulationThread = new Thread(() -> {
            System.out.println("Simulation thread starting");
            engine.run();
        });

        simulationThread.start();
    }



    public void pauseSimulation() {
        engine.setPaused(!engine.isPaused());
        if (engine.isPaused()) {
            view.setSimulationStatus("Status: Paused");
        } else {
            view.setSimulationStatus("Status: Running");
        }
    }

    public void resetSimulation() {
        if (simulationThread != null) {
            simulationThread.interrupt();
            simulationThread = null;
        }
        Platform.runLater(() -> {
            view.showSimulationReset();  // New method to add to SimulatorView
        });
        updateStatus("Simulation reset");
    }



    public void setNumberOfStations(int stations) {
        this.numberOfStations = stations;
        if (engine != null) {
            if (simulationThread != null) {
                simulationThread.interrupt();
            }
            engine = new MyEngine(this);
            engine.setQueueUpdateListener(newStatus -> updateQueueStatus(newStatus));
            engine.setSimulationTime(1000);

            startSimulation();
        }
        updateStatus("Number of stations set to: " + stations);
    }


    private void updateStatus(String message) {
        Platform.runLater(() -> {
            view.setSimulationStatus("Status: " + message);
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
        // Convert slider value (0-100) to sleep time (100ms - 0ms)

        this.sleepTime = Math.max(1, (long)(100 - (sliderValue)));
    }

    public long getSleepTime() {
        return sleepTime;
    }
    public double getSimulationTime() {
        return simulationTime;
    }
    public double getSimulationSpeed() {
        return simulationSpeed;
    }

    public int getNumberOfStations() {
        return numberOfStations;
    }


    public void onSimulationComplete(String stats) {
        Platform.runLater(() -> {
            view.showSimulationComplete();
            view.updateStatistics(stats);
        });
    }

    public void onSimulationPaused() {
        Platform.runLater(() -> {
            view.setSimulationStatus("Status: Paused");
        });
    }

    public void setArrivalInterval(double interval) {
        this.arrivalInterval = interval;
        if (engine != null) {
            engine.setArrivalInterval(interval);
        }
    }

    public double getArrivalInterval() {
        return arrivalInterval;
    }

 public class DatabaseHelper {
        private static final String URL = "jdbc:databaseurl";
        private static final String USER = "username";
        private static final String PASSWORD = "password";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}
