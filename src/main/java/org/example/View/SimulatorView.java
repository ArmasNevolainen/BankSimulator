package org.example.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.controller.SimulatorController;
import org.example.model.Customer;
import java.util.List;
import java.util.Map;

public class SimulatorView extends Application {
    private boolean isSimulationComplete = false;
    private boolean isUpdating = false;
    private SimulatorController controller = new SimulatorController(this);
    private Button startButton = new Button("Start");
    private Button pauseButton = new Button("Pause");
    private Button resetButton = new Button("Reset");
    private Slider simulationTimeSlider = new Slider(100, 2000, 1000);
    private Slider speedSlider = new Slider(0, 100, 50);
    private Slider clientDistributionSlider = new Slider(0, 100, 80);
    private Label speedLabel = new Label("Simulation Speed:");
    private ComboBox<String> stationsSelector = new ComboBox<>();
    private ComboBox<String> accountStationsSelector = new ComboBox<>();
    private Slider intervalSlider = new Slider(1, 10, 5);
    private Slider serviceTimeSlider = new Slider(5, 30, 10);
    private Slider accountServiceTimeSlider = new Slider(10, 60, 15);
    private Label intervalLabel = new Label("Client Arrival Interval:");
    private TextArea statusArea = new TextArea();
    private Canvas simulationCanvas;
    private Label simulationStatusLabel = new Label("Status: Ready");
    private ScrollPane statsScrollPane = new ScrollPane();
    private VBox statsPanel = new VBox(5);
    private int margin = 50;
    private ComboBox<String> resultsHistoryBox = new ComboBox<>();
    //Images
    private Image automate;
    private Image teller;
    private Image accountTeller;
    private Image accountClient;
    private Image transactionClient;

    @Override
    public void start(Stage stage) {
        automate = new Image(getClass().getResource("/QueueAutomate.png").toExternalForm());
        teller = new Image(getClass().getResource("/Teller.png").toExternalForm());
        accountTeller = new Image(getClass().getResource("/accountTeller.png").toExternalForm());
        accountClient = new Image(getClass().getResource("/AClient.png").toExternalForm());
        transactionClient = new Image(getClass().getResource("/TClient.png").toExternalForm());
        BorderPane mainLayout = new BorderPane();

        VBox topContainer = new VBox();
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setMaxWidth(Double.MAX_VALUE);

        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setMaxWidth(600);

        startButton.setPrefWidth(100);
        pauseButton.setPrefWidth(100);
        resetButton.setPrefWidth(100);

        controlPanel.getChildren().addAll(startButton, pauseButton, resetButton);
        topContainer.getChildren().add(controlPanel);
        mainLayout.setTop(topContainer);

        // Settings Panel
        VBox settingsPanel = new VBox(5);
        settingsPanel.setPadding(new Insets(5));
        settingsPanel.setPrefWidth(200);
        BorderPane.setMargin(settingsPanel, new Insets(20));

        // Sliders
        simulationTimeSlider.setShowTickLabels(true);
        simulationTimeSlider.setShowTickMarks(true);
        simulationTimeSlider.setMajorTickUnit(500);
        simulationTimeSlider.setBlockIncrement(100);
        clientDistributionSlider.setShowTickLabels(true);
        clientDistributionSlider.setShowTickMarks(true);
        clientDistributionSlider.setMajorTickUnit(10);
        clientDistributionSlider.setMinorTickCount(1);
        clientDistributionSlider.setSnapToTicks(true);
        clientDistributionSlider.setValue(80);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        stationsSelector.getItems().addAll("1 Station", "2 Stations", "3 Stations", "4 Stations", "5 Stations");
        stationsSelector.setValue("2 Stations");
        accountStationsSelector.getItems().addAll("1 Station", "2 Stations", "3 Stations", "4 Stations", "5 Stations");
        accountStationsSelector.setValue("1 Station");
        intervalSlider.setShowTickLabels(true);
        intervalSlider.setShowTickMarks(true);
        intervalSlider.setMajorTickUnit(1);
        intervalSlider.setBlockIncrement(1);
        intervalSlider.setSnapToTicks(true);
        simulationStatusLabel.setStyle("-fx-font-weight: bold;");
        serviceTimeSlider.setShowTickLabels(true);
        serviceTimeSlider.setShowTickMarks(true);
        serviceTimeSlider.setMajorTickUnit(5);
        serviceTimeSlider.setBlockIncrement(1);
        accountServiceTimeSlider.setShowTickLabels(true);
        accountServiceTimeSlider.setShowTickMarks(true);
        accountServiceTimeSlider.setMajorTickUnit(10);
        accountServiceTimeSlider.setBlockIncrement(5);
        topContainer.getChildren().add(simulationStatusLabel);

        settingsPanel.getChildren().addAll(

                new Label("Simulation Settings: "),
                new Label("Simulation Time (min):"),
                simulationTimeSlider,
                speedLabel,
                speedSlider,
                new Label("Transaction Teller Stations:"),
                stationsSelector,
                new Label("Account Teller Stations:"),
                accountStationsSelector,
                intervalLabel,
                intervalSlider,
                new Label("Transaction Clients %:"),
                clientDistributionSlider,
                new Label("Transaction Service Time (min):"),
                serviceTimeSlider,
                new Label("Account Service Time (min):"),
                accountServiceTimeSlider

        );
        // Canvas
        simulationCanvas = new Canvas(650, 400);
        VBox canvasContainer = new VBox();
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setMaxWidth(Double.MAX_VALUE);
        StackPane canvasWrapper = new StackPane(simulationCanvas);
        canvasWrapper.setStyle("-fx-background-color: white;");
        canvasWrapper.setPadding(new Insets(10));
        canvasWrapper.setMaxWidth(650);
        canvasWrapper.setMaxHeight(400);

        canvasContainer.getChildren().add(canvasWrapper);
        mainLayout.setCenter(canvasContainer);

        statusArea.setPrefRowCount(3);
        statusArea.setEditable(false);
        statusArea.setText("Simulation ready to start...");

        mainLayout.setTop(controlPanel);
        mainLayout.setLeft(settingsPanel);
        mainLayout.setCenter(canvasWrapper);
        mainLayout.setBottom(statusArea);

        Scene scene = new Scene(mainLayout, 1200, 630);
        stage.setTitle("Simulation Control Panel");
        stage.setScene(scene);

        VBox historyPanel = new VBox(5);
        Label historyLabel = new Label("Previous Results:");
        resultsHistoryBox.setMaxWidth(230);
        historyPanel.getChildren().addAll(historyLabel, resultsHistoryBox);
        statsPanel.getChildren().add(0, historyPanel);
        // Stats panel
        statsPanel.setPadding(new Insets(10));
        statsPanel.setPrefWidth(250);
        statsPanel.setStyle("-fx-background-color: #f0f0f0;");
        statsPanel.setPadding(new Insets(10, 20, 0, 10));
        statsScrollPane.setPadding(new Insets(0, 10, 0, 0));
        statsScrollPane.setContent(statsPanel);
        statsScrollPane.setFitToWidth(true);
        statsScrollPane.setPrefViewportWidth(250);
        statsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        statsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainLayout.setRight(statsScrollPane);

        // Initial draw
        drawBaseElements();
        updateResultsHistory();
        stage.show();

        startButton.setOnAction(e -> {
            simulationTimeSlider.setDisable(true);
            clientDistributionSlider.setDisable(true);
            stationsSelector.setDisable(true);
            accountStationsSelector.setDisable(true);
            intervalSlider.setDisable(true);
            serviceTimeSlider.setDisable(true);
            accountServiceTimeSlider.setDisable(true);
            controller.setClientDistribution(clientDistributionSlider.getValue());
            controller.startSimulation();
        });
        pauseButton.setOnAction(e -> {
            controller.pauseSimulation();
            updatePauseButton(pauseButton.getText().equals("Pause"));
        });
        resetButton.setOnAction(e -> {
            simulationTimeSlider.setDisable(false);
            clientDistributionSlider.setDisable(false);
            stationsSelector.setDisable(false);
            accountStationsSelector.setDisable(false);
            intervalSlider.setDisable(false);
            serviceTimeSlider.setDisable(false);
            accountServiceTimeSlider.setDisable(false);
            controller.resetSimulation();
        });

        simulationTimeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setSimulationTime(newVal.doubleValue())
        );

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setSimulationSpeed(newVal.doubleValue()));



        stationsSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateStationSelectors(true);
            }
        });

        accountStationsSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateStationSelectors(false);
            }
        });

        intervalSlider.setOnMouseReleased(event ->
                controller.setArrivalInterval(intervalSlider.getValue())
        );
        serviceTimeSlider.setOnMouseReleased(event ->
                controller.setTransactionServiceTime(serviceTimeSlider.getValue())
        );
        accountServiceTimeSlider.setOnMouseReleased(event ->
                controller.setAccountServiceTime(accountServiceTimeSlider.getValue())
        );

        resultsHistoryBox.setOnAction(e -> {
            String selected = resultsHistoryBox.getValue();
            loadHistoricalResults(selected);
        });

    }

    public boolean isSimulationComplete() {
        return simulationStatusLabel.getText().contains("Complete");
    }

    public void updateQueueVisualization(Map<String, List<Customer>> queueStatus) {
        Platform.runLater(() -> {
            if (isSimulationComplete()) {
                return;
            }
            GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

            drawBaseElements();
            drawCustomerQueues(controller.convertQueueStatusToSigns(queueStatus));
        });
    }
    private void drawBaseElements() {
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
        double canvasHeight = simulationCanvas.getHeight();
        int numTransactionTellers = controller.getNumberOfStations();
        int numAccountTellers = controller.getNumberOfAccountStations();
        int tellerWidth = 40;
        int tellerHeight = 50;
        int tellerX = 500;

        // Calculate spacing for all tellers
        double usableHeight = canvasHeight - (2 * margin) - tellerHeight;
        double spacing = usableHeight / (numTransactionTellers + numAccountTellers - 1);

        // Draw background and automat
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());
        gc.drawImage(automate, 250, (canvasHeight - 50) / 2, 30, 50);

        // Draw transaction tellers
        for(int i = 0; i < numTransactionTellers; i++) {
            double yPosition = margin + (spacing * i);
            gc.drawImage(teller, tellerX, yPosition, tellerWidth, tellerHeight);
        }

        // Draw account tellers
        for(int i = 0; i < numAccountTellers; i++) {
            double yPosition = margin + (spacing * (numTransactionTellers + i));
            gc.drawImage(accountTeller, tellerX, yPosition, tellerWidth, tellerHeight);
        }
    }


    private void drawCustomerQueues(Map<String, List<Integer>> queueStatus) {
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
        double canvasHeight = simulationCanvas.getHeight();
        int numTransactionTellers = controller.getNumberOfStations();
        int numAccountTellers = controller.getNumberOfAccountStations();

        double usableHeight = canvasHeight - (2 * margin) - 50;
        double spacing = usableHeight / (numTransactionTellers + numAccountTellers - 1);

        // Draw automat queue
        List<Integer> automatQueue = queueStatus.get("automat");
        if (automatQueue != null) {
            drawQueueCustomers(gc, automatQueue, 220, (int)(canvasHeight - 50) / 2, true);
        }

        // Draw transaction teller queues
        for(int i = 1; i <= numTransactionTellers; i++) {
            List<Integer> tellerQueue = queueStatus.get("teller" + i);
            if (tellerQueue != null) {
                double yPosition = margin + (spacing * (i-1));
                drawQueueCustomers(gc, tellerQueue, 450, (int)yPosition, true);
            }
        }

        // Draw account teller queues
        for(int i = 1; i <= numAccountTellers; i++) {
            List<Integer> accountQueue = queueStatus.get("account" + i);
            if (accountQueue != null) {
                double yPosition = margin + (spacing * (numTransactionTellers + i - 1));
                drawQueueCustomers(gc, accountQueue, 450, (int)yPosition, true);
            }
        }
    }



    private void drawCustomers(GraphicsContext gc, int clientSign, int x, int y) {
        gc.drawImage(clientSign == 1 ? transactionClient : accountClient, x, y, 20, 40);
        gc.fillOval(x, y, 10, 10);
    }

    private void drawQueueCustomers(GraphicsContext gc, List<Integer> clientSigns, int startX, int startY, boolean horizontal) {
        if (clientSigns == null) return;

        if (clientSigns.size() <= 10) {
            for (int i = 0; i < clientSigns.size(); i++) {
                int x = startX - (i * 20);
                int y = startY + 10;
                gc.drawImage(clientSigns.get(i) == 1 ? transactionClient : accountClient, x, y, 20, 40);
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int x = startX - (i * 20);
                int y = startY + 10;
                gc.drawImage(clientSigns.get(i) == 1 ? transactionClient : accountClient, x, y, 20, 40);
            }

            int x = startX - (9 * 20);
            int y = startY + 25;
            gc.setFill(Color.BLACK);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 20));
            String text = "+" + (clientSigns.size() - 9);
            gc.strokeText(text, x, y);
            gc.fillText(text, x, y);
        }
    }

    public void showSimulationComplete() {
        Platform.runLater(() -> {
            simulationStatusLabel.setText("Status: Simulation Complete");
            statusArea.setText("Simulation Complete ");
            startButton.setDisable(true);
            pauseButton.setDisable(true);
            resetButton.setDisable(false);
        });
    }
    public void showSimulationReset() {
        startButton.setDisable(false);
        pauseButton.setDisable(false);
        stationsSelector.setDisable(false);
        simulationStatusLabel.setText("Status: Ready");
        updateStatistics("");
    }

    public void updateStatistics(String stats) {
        Platform.runLater(() -> {
            // Clear panel
            statsPanel.getChildren().clear();

            // First add history section
            Label historyLabel = new Label("Previous Results:");
            historyLabel.setStyle("-fx-font-weight: bold;");
            statsPanel.getChildren().add(historyLabel);
            statsPanel.getChildren().add(resultsHistoryBox);

            // Then add current statistics if provided
            if (stats != null && !stats.isEmpty()) {
                Label titleLabel = new Label("Simulation Statistics");
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                statsPanel.getChildren().add(titleLabel);

                String[] statLines = stats.split("\n");
                for (String line : statLines) {
                    Label statLabel = new Label(line);
                    statLabel.setWrapText(true);
                    statLabel.setMaxWidth(230);
                    statLabel.setStyle("-fx-padding: 2px 0;");
                    statsPanel.getChildren().add(statLabel);
                }
            }
        });
    }
    public void setSimulationStatus(String status) {
        simulationStatusLabel.setText(status);
    }

    public void updatePauseButton(boolean isPaused) {
        Platform.runLater(() -> {
            pauseButton.setText(isPaused ? "Resume" : "Pause");
        });
    }
    private void updateStationSelectors(boolean isTransactionUpdate) {
        if (isUpdating) return;

        try {
            isUpdating = true;
            int total = 6;
            int currentValue, maxOtherValue, newOtherValue;
            ComboBox<String> updatingBox, otherBox;

            if (isTransactionUpdate) {
                updatingBox = stationsSelector;
                otherBox = accountStationsSelector;
            } else {
                updatingBox = accountStationsSelector;
                otherBox = stationsSelector;
            }

            currentValue = Integer.parseInt(updatingBox.getValue().split(" ")[0]);
            maxOtherValue = total - currentValue;
            int currentOther = Integer.parseInt(otherBox.getValue().split(" ")[0]);
            newOtherValue = Math.min(currentOther, maxOtherValue);

            // Update other ComboBox
            otherBox.getItems().clear();
            for(int i = 1; i <= maxOtherValue; i++) {
                otherBox.getItems().add(i + (i == 1 ? " Station" : " Stations"));
            }
            otherBox.setValue(newOtherValue + (newOtherValue == 1 ? " Station" : " Stations"));

            // Update controller and redraw
            if (isTransactionUpdate) {
                controller.setStationNumbers(newOtherValue, currentValue);
            } else {
                controller.setStationNumbers(currentValue, newOtherValue);
            }
            drawBaseElements();

        } catch (Exception e) {
            System.err.println("Error updating stations: " + e.getMessage());
        } finally {
            isUpdating = false;
        }
    }

    public void updateStatusArea(int totalCustomers) {
        Platform.runLater(() -> {
            statusArea.setText("Simulation running...\nTotal customers served: " + totalCustomers);
        });
    }

    public void updateResultsHistory() {
        Platform.runLater(() -> {
            String currentSelection = resultsHistoryBox.getValue();
            resultsHistoryBox.getItems().clear();
            resultsHistoryBox.getItems().addAll(controller.getHistoricalResultTimestamps());
            if (currentSelection != null && resultsHistoryBox.getItems().contains(currentSelection)) {
                resultsHistoryBox.setValue(currentSelection);
            }
        });
    }

    private void loadHistoricalResults(String timestamp) {
        if (timestamp != null) {
            String results = controller.getResultsByTimestamp(timestamp);
            if (!results.isEmpty()) {
                updateStatistics(results);
            }
        }
    }



}
