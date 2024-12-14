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

/**
 * Controls the bank simulation system, managing the interaction between the simulation model and view.
 * Handles simulation configuration, execution, and state management.
 *
 * @author Group 3
 * @version 1.0
 */
public class SimulatorController {
    private final SimulatorView view;
    private MyEngine engine;
    private Thread simulationThread;
    private final double simulationSpeed = 1.0;
    private int numberOfStations = 2;
    private int numberOfAccountStations = 1;
    private double arrivalInterval = 5.0;
    private long sleepTime = 100;// Default sleep time in milliseconds
    private double simulationTime = 1000.0;
    private double clientDistributionPercentage = 80.0;
    private double transactionServiceTime = 10.0;
    private double accountServiceTime = 15.0;
    private final DatabaseService dbService = new DatabaseService();

    /**
     * Constructs a new SimulatorController with the specified view.
     *
     * @param view The SimulatorView instance to be controlled
     */
    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    /**
     * Set the percentage of clients that are transaction clients.
     * @param percentage The percentage (0-100) of clients that will be transaction clients
     */
    public void setClientDistribution(double percentage) {
        this.clientDistributionPercentage = percentage;
    }
    /**
     * Get the percentage of clients that are transaction clients.
     * @return The percentage (0-100) of clients that are transaction clients
     */
    public double getClientDistribution() {
        return clientDistributionPercentage;
    }
    /**
     * Start the simulation.
     */
    public void startSimulation() {
        Customer.resetCustomerCount();
        System.gc();
        System.out.println("Starting simulation...");


        Clock.getInstance().reset();

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
    /**
     * Set the simulation time.
     * @param time The duration in minutes for which the simulation will run
     */
    public void setSimulationTime(double time) {
        this.simulationTime = time;
        if (engine != null) {
            engine.setSimulationTime(time);
        }
    }
    /**
     * Pause the simulation.
     */
    public void pauseSimulation() {
        engine.setPaused(!engine.isPaused());
        if (engine.isPaused()) {
            view.setSimulationStatus("Status: Paused");
        } else {
            view.setSimulationStatus("Status: Running");
        }
    }
    /**
     * Reset the simulation.
     */
    public void resetSimulation() {
        if (simulationThread != null) {
            simulationThread.interrupt();
            simulationThread = null;
        }
        Platform.runLater(() -> {
            view.showSimulationReset();
        });
        updateStatus("Simulation reset");
    }
    /**
     * Set the number of stations.
     * @param accountStations The number of account service stations to set up
     * @param transactionStations The number of transaction service stations to set up
     */
    public void setStationNumbers(int accountStations, int transactionStations) {
        this.numberOfAccountStations = accountStations;
        this.numberOfStations = transactionStations;
        updateStatus("Stations set to: " + transactionStations + " transaction, " + accountStations + " account");
    }

    /**
     * Update the status of the simulation.
     * @param message
     */
    private void updateStatus(String message) {
        Platform.runLater(() -> {
            view.setSimulationStatus("Status: " + message);
        });
    }
    private Map<String, List<Customer>> currentQueueStatus = new HashMap<>();
    /**
     * Update the queue status.
     * @param newStatus The current state of all queues in the system as a map of queue names to customer lists
     */
    public void updateQueueStatus(Map<String, List<Customer>> newStatus) {
        System.out.println("Controller received update: " + newStatus);
        this.currentQueueStatus = newStatus;
        System.out.println("Queue status updated: " + newStatus.size() + " queues");
        Platform.runLater(() -> view.updateQueueVisualization(currentQueueStatus));
    }

    /**
     * Get the queue status.
     * @return A map containing all queues in the system, where keys are queue names and values are lists of customers in each queue
     */
    public Map<String, List<Customer>> getQueueStatus() {
        return currentQueueStatus;
    }
    /**
     * Convert the queue status to signs.
     * @param queueStatus The current queue status map to be converted
     * @return A map where queue names are mapped to lists of integers (1 for transaction clients, 2 for account clients)
     */
    public Map<String, List<Integer>> convertQueueStatusToSigns(Map<String, List<Customer>> queueStatus) {
        return queueStatus.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(customer -> customer.getType() == CustomerType.TRANSACTION_CLIENT ? 1 : 2)
                                .collect(Collectors.toList())
                ));
    }
    /**
     * Set the simulation speed.
     * @param sliderValue The speed value from slider (0-100) where 0 is slowest (100ms sleep) and 100 is fastest (1ms sleep)
     */
    public void setSimulationSpeed(double sliderValue) {
        // Convert slider value (0-100) to sleep time (100ms - 0ms)

        this.sleepTime = Math.max(1, (long)(100 - (sliderValue)));
    }
    /**
     * Get the sleep time.
     * @return The current sleep time in milliseconds between simulation steps (1-100ms)
     */
    public long getSleepTime() {
        return sleepTime;
    }
    /**
     * Get the simulation time.
     * @return The total duration in minutes for which the simulation will run
     */
    public double getSimulationTime() {
        return simulationTime;
    }
    /**
     * Get the simulation speed.
     * @return The current simulation speed multiplier where higher values indicate faster execution
     */
    public double getSimulationSpeed() {
        return simulationSpeed;
    }
    /**
     * Get the number of stations.
     * @return The number of transaction service stations currently configured in the simulation
     */
    public int getNumberOfStations() {
        return numberOfStations;
    }
    /**
     * Get the number of account stations.
     * @return The number of account service stations currently configured in the simulation
     */
    public int getNumberOfAccountStations() {
        return numberOfAccountStations;
    }
    /**
     Saves the simulation results to a database and updates the view.
     * @param stats The final simulation statistics and results as a formatted string
     */
    public void onSimulationComplete(String stats) {
        saveSimulationResults(stats);
        Platform.runLater(() -> {
            view.showSimulationComplete();
            view.updateStatistics(stats);
        });
    }

    /**
     * Updates the view when the simulation is paused.
     */

    public void onSimulationPaused() {
        Platform.runLater(() -> {
            view.setSimulationStatus("Status: Paused");
        });
    }

    /**
     * Sets the arrival interval for customers in the simulation.
     *
     * @param interval The time interval between customer arrivals in minutes
     */
    public void setArrivalInterval(double interval) {
        this.arrivalInterval = interval;
        if (engine != null) {
            engine.setArrivalInterval(interval);
        }
    }
    /**
     * Retrieves the current arrival interval setting.
     *
     * @return The current arrival interval in minutes
     */
    public double getArrivalInterval() {
        return arrivalInterval;
    }

    /**
     * Retrieves the current transaction service time setting.
     *
     * @return The current transaction service time in minutes
     */
    public double getTransactionServiceTime() {
        return transactionServiceTime;
    }
    /**
     * Sets the transaction service time for the simulation.
     *
     * @param time The time in minutes required to service a transaction customer
     */
    public void setTransactionServiceTime(double time) {
        this.transactionServiceTime = time;
    }
    /**
     * Retrieves the current account service time setting.
     *
     * @return The current account service time in minutes
     */
    public double getAccountServiceTime() {
        return accountServiceTime;
    }
    /**
     * Sets the account service time for the simulation.
     *
     * @param time The time in minutes required to service an account customer
     */
    public void setAccountServiceTime(double time) {
        this.accountServiceTime = time;
    }
    /**
     * Sets the number of transaction service stations in the simulation.
     *
     * @param stations The number of transaction service stations to set up
     */
    public void setNumberOfAccountStations(int stations) {
        this.numberOfAccountStations = stations;
        updateStatus("Account stations set to: " + stations);
    }

    /**
     * updates the status area in the view with the current customer count.
     * @param count The current customer count
     */
    public void updateCustomerCount(int count) {
        view.updateStatusArea(count);
    }
    /**
     * Saves the simulation results to the database.
     *
     * @param stats The statistics string containing simulation results
     */
    public void saveSimulationResults(String stats) {
        String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
        SimulationResult result = new SimulationResult(0, timestamp, stats);
        dbService.saveSimulationResult(result);
        view.updateResultsHistory();
    }
    /**
     * Retrieves historical simulation result timestamps.
     *
     * @return A list of timestamps as strings
     */

    public List<String> getHistoricalResultTimestamps() {
        List<String> timestamps = dbService.getSimulationResults()
                .stream()
                .map(SimulationResult::getTimestamp)
                .collect(Collectors.toList());
        System.out.println("Retrieved timestamps: " + timestamps); // Debug print
        return timestamps;
    }
    /**
     * Retrieves historical simulation results by timestamp.
     *
     * @param timestamp The timestamp of the desired simulation result
     * @return The simulation results as a string, or empty string if not found
     */
    public String getResultsByTimestamp(String timestamp) {
        return dbService.getSimulationResults()
                .stream()
                .filter(r -> r.getTimestamp().equals(timestamp))
                .map(SimulationResult::getResults)
                .findFirst()
                .orElse("");
    }


}
