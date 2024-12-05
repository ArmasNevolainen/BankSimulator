package org.example.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private SimulatorController controller = new SimulatorController(this);
    private Button startButton = new Button("Start");
    private Button pauseButton = new Button("Pause");
    private Button resetButton = new Button("Reset");
    private Slider speedSlider = new Slider(0, 100, 50);
    private Slider clientDistributionSlider = new Slider(0, 100, 80);
    private Label speedLabel = new Label("Simulation Speed:");
    private ComboBox<String> stationsSelector = new ComboBox<>();
    private Slider intervalSlider = new Slider(1, 10, 5);
    private Label intervalLabel = new Label("Client Arrival Interval:");
    private TextArea statusArea = new TextArea();
    private Canvas simulationCanvas;
    private Label simulationStatusLabel = new Label("Status: Ready");
    private ScrollPane statsScrollPane = new ScrollPane();
    private VBox statsPanel = new VBox(5);
    private int margin = 50;
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
        controlPanel.setMaxWidth(600); // Set a maximum width for the control panel

        startButton.setPrefWidth(100);
        pauseButton.setPrefWidth(100);
        resetButton.setPrefWidth(100);

        controlPanel.getChildren().addAll(startButton, pauseButton, resetButton);
        topContainer.getChildren().add(controlPanel);
        mainLayout.setTop(topContainer);

        // Settings Panel
        VBox settingsPanel = new VBox(10);
        settingsPanel.setPadding(new Insets(10));
        settingsPanel.setPrefWidth(200); // Fixed width for controls

        // Sliders
        clientDistributionSlider.setShowTickLabels(true);
        clientDistributionSlider.setShowTickMarks(true);
        clientDistributionSlider.setMajorTickUnit(10);
        clientDistributionSlider.setMinorTickCount(1);
        clientDistributionSlider.setSnapToTicks(true);
        clientDistributionSlider.setValue(80);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        stationsSelector.getItems().addAll("3 Stations", "4 Stations", "5 Stations", "6 Stations", "7 Stations");
        stationsSelector.setValue("3 Stations");
        intervalSlider.setShowTickLabels(true);
        intervalSlider.setShowTickMarks(true);
        intervalSlider.setMajorTickUnit(1);
        intervalSlider.setBlockIncrement(1);
        intervalSlider.setSnapToTicks(true);
        simulationStatusLabel.setStyle("-fx-font-weight: bold;");
        topContainer.getChildren().add(simulationStatusLabel);

        settingsPanel.getChildren().addAll(
                new Label("Simulation Settings: "),
                speedLabel,
                speedSlider,
                new Label("Stations:"),
                stationsSelector,
                intervalLabel,
                intervalSlider,
                new Label("Transaction Clients %:"),
                clientDistributionSlider
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

        Scene scene = new Scene(mainLayout, 1200, 700);
        stage.setTitle("Simulation Control Panel");
        stage.setScene(scene);

        // Stats panel
        statsPanel.setPadding(new Insets(10));
        statsPanel.setPrefWidth(250);
        statsPanel.setStyle("-fx-background-color: #f0f0f0;");
        statsPanel.setPadding(new Insets(10, 20, 10, 10));
        statsScrollPane.setPadding(new Insets(0, 10, 0, 0));
        statsScrollPane.setContent(statsPanel);
        statsScrollPane.setFitToWidth(true);
        statsScrollPane.setPrefViewportWidth(250);
        statsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        statsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainLayout.setRight(statsScrollPane);

        // Initial draw
        drawBaseElements();

        stage.show();

        startButton.setOnAction(e -> {
            clientDistributionSlider.setDisable(true);
            controller.setClientDistribution(clientDistributionSlider.getValue());
            controller.startSimulation();
        });
        pauseButton.setOnAction(e -> {
            controller.pauseSimulation();
            updatePauseButton(pauseButton.getText().equals("Pause"));
        });
        resetButton.setOnAction(e -> {
            clientDistributionSlider.setDisable(false);
            controller.resetSimulation();
        });

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setSimulationSpeed(newVal.doubleValue()));

        stationsSelector.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setNumberOfStations(Integer.parseInt(newVal.split(" ")[0])));

        intervalSlider.setOnMouseReleased(event ->
                controller.setArrivalInterval(intervalSlider.getValue())
        );
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
        int activeStations = controller.getNumberOfStations() + 1; // +1 for account teller
        int tellerWidth = 40;
        int tellerHeight = 50;
        int tellerX = 500;

        // Calculate spacing to fit within visible area
        double usableHeight = canvasHeight - (2 * margin) - tellerHeight;
        double spacing = usableHeight / (activeStations - 1);

        // Canvas background
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

        // Queue automat
        gc.drawImage(automate, 200, (canvasHeight - 50) / 2, 30, 50);

        // Regular tellers
        for(int i = 0; i < activeStations - 1; i++) {
            double yPosition = margin + (spacing * i);
            gc.drawImage(teller, tellerX, yPosition, tellerWidth, tellerHeight);
        }

        // Account teller at the last position
        gc.drawImage(accountTeller, tellerX, canvasHeight - margin - tellerHeight, tellerWidth, tellerHeight);
    }


    private void drawCustomerQueues(Map<String, List<Integer>> queueStatus) {
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
        double canvasHeight = simulationCanvas.getHeight();
        int activeStations = controller.getNumberOfStations() + 1; // +1 for account teller

        // Calculate same spacing as used for tellers
        double usableHeight = canvasHeight - (2 * margin) - 50;
        double spacing = usableHeight / (activeStations - 1);

        // Automat queue
        List<Integer> automatQueue = queueStatus.get("automat");
        if (automatQueue != null) {
            drawQueueCustomers(gc, automatQueue, 150, (int)(canvasHeight - 50) / 2, true);
        }

        // Transaction teller queues
        for(int i = 1; i <= controller.getNumberOfStations(); i++) {
            List<Integer> tellerQueue = queueStatus.get("teller" + i);
            if (tellerQueue != null) {
                double yPosition = margin + (spacing * (i-1));
                drawQueueCustomers(gc, tellerQueue, 450, (int)yPosition, true);
            }
        }

        // Account teller queue
        List<Integer> accountQueue = queueStatus.get("account");
        if (accountQueue != null) {
            drawQueueCustomers(gc, accountQueue, 450, (int)(canvasHeight - margin - 50), true);
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
        simulationStatusLabel.setText("Status: Simulation Complete");
        startButton.setDisable(true);
        pauseButton.setDisable(true);

        // Enable reset button to start new simulation
        resetButton.setDisable(false);
    }
    public void showSimulationReset() {
        startButton.setDisable(false);
        pauseButton.setDisable(false);
        simulationStatusLabel.setText("Status: Ready");
        updateStatistics("");
    }

    public void updateStatistics(String stats) {
        Platform.runLater(() -> {
            statsPanel.getChildren().clear();

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
        });
    }
    public void setSimulationStatus(String status) {
        simulationStatusLabel.setText(status);
    }

    // Add this method to update the pause button text based on simulation state
    public void updatePauseButton(boolean isPaused) {
        Platform.runLater(() -> {
            pauseButton.setText(isPaused ? "Resume" : "Pause");
        });
    }





}
