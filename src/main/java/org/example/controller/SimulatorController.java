package org.example.controller;

import org.example.View.SimulatorView;
import org.example.database.DatabaseService;
import org.example.framework.Clock;
import org.example.framework.Trace;
import org.example.model.Customer;
import org.example.model.CustomerType;
import org.example.model.MyEngine;
import javafx.application.Platform;
import org.example.model.SimulationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulatorController {
    private SimulatorView view;
    private MyEngine engine;
    private Thread simulationThread;
    private boolean isPaused = false;
    private double simulationSpeed = 1.0;
    private int numberOfStations = 3;
    private int numberOfAccountStations = 1;
    private double arrivalInterval = 5.0;
    private long sleepTime = 100;// Default sleep time in milliseconds
    private double simulationTime = 1000.0;
    private double clientDistributionPercentage = 80.0;
    private double transactionServiceTime = 10.0;
    private double accountServiceTime = 15.0;
    private DatabaseService dbService = new DatabaseService();


    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    public void setClientDistribution(double percentage) {
        this.clientDistributionPercentage = percentage;
    }

    public double getClientDistribution() {
        return clientDistributionPercentage;
    }

    public void startSimulation() {
        Customer.resetCustomerCount();
        System.gc();
        System.out.println("Starting simulation...");

        // Reset the clock to 0
        Clock.getInstance().reset();

        // Create fresh engine instance
        Trace.setTraceLevel(Trace.Level.INFO);
        engine = new MyEngine(this);
        engine.setQueueUpdateListener(newStatus -> updateQueueStatus(newStatus));
        engine.setSimulationTime(simulationTime);

        // Start new simulation thread
        simulationThread = new Thread(() -> {
            System.out.println("Simulation thread starting");
            engine.run();
        });

        simulationThread.start();
    }

    public void setSimulationTime(double time) {
        this.simulationTime = time;
        if (engine != null) {
            engine.setSimulationTime(time);
        }
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
        updateStatus("Number of stations set to: " + stations);
    }



    public void setStationNumbers(int accountStations, int transactionStations) {
        this.numberOfAccountStations = accountStations;
        this.numberOfStations = transactionStations;
        updateStatus("Stations set to: " + transactionStations + " transaction, " + accountStations + " account");
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

    public int getNumberOfAccountStations() {
        return numberOfAccountStations;
    }

    public void onSimulationComplete(String stats) {
        saveSimulationResults(stats);
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

    public double getTransactionServiceTime() {
        return transactionServiceTime;
    }

    public void setTransactionServiceTime(double time) {
        this.transactionServiceTime = time;
    }
    public double getAccountServiceTime() {
        return accountServiceTime;
    }

    public void setAccountServiceTime(double time) {
        this.accountServiceTime = time;
    }
    public void setNumberOfAccountStations(int stations) {
        this.numberOfAccountStations = stations;
        updateStatus("Account stations set to: " + stations);
    }
    public void updateCustomerCount(int count) {
        view.updateStatusArea(count);
    }

    public void saveSimulationResults(String stats) {
        String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
        SimulationResult result = new SimulationResult(0, timestamp, stats);
        dbService.saveSimulationResult(result);
        view.updateResultsHistory();
    }

    public List<String> getHistoricalResultTimestamps() {
        List<String> timestamps = dbService.getSimulationResults()
                .stream()
                .map(SimulationResult::getTimestamp)
                .collect(Collectors.toList());
        System.out.println("Retrieved timestamps: " + timestamps); // Debug print
        return timestamps;
    }

    public String getResultsByTimestamp(String timestamp) {
        return dbService.getSimulationResults()
                .stream()
                .filter(r -> r.getTimestamp().equals(timestamp))
                .map(SimulationResult::getResults)
                .findFirst()
                .orElse("");
    }


}
