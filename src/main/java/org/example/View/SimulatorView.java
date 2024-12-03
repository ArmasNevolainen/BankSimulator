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
    private Label speedLabel = new Label("Simulation Speed:");
    private ComboBox<String> stationsSelector = new ComboBox<>();
    private Slider intervalSlider = new Slider(1, 10, 5);
    private Label intervalLabel = new Label("Client Arrival Interval:");
    private TextArea statusArea = new TextArea();
    private Canvas simulationCanvas;
    private Label simulationStatusLabel = new Label("Status: Ready");
    private VBox statsPanel = new VBox(10);
    private int margin = 50;

    @Override
    public void start(Stage stage) {
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
                intervalSlider
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

        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setTitle("Simulation Control Panel");
        stage.setScene(scene);

        // Stats panel
        statsPanel.setPadding(new Insets(10));
        statsPanel.setPrefWidth(200);
        statsPanel.setStyle("-fx-background-color: #f0f0f0;");
        mainLayout.setRight(statsPanel);


        // Initial draw
        drawBaseElements();

        stage.show();

        startButton.setOnAction(e -> controller.startSimulation());
        pauseButton.setOnAction(e -> {
            controller.pauseSimulation();
            updatePauseButton(pauseButton.getText().equals("Pause"));
        });
        resetButton.setOnAction(e -> controller.resetSimulation());

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
        int activeStations = controller.getNumberOfStations();
        int tellerWidth = 50;
        int tellerHeight = 60;
        int tellerX = 500;

        double totalSpace = canvasHeight - 2 * margin - tellerHeight;
        double spacing = totalSpace / (activeStations - 1);

        // Canvas background
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

        // Queue automat
        Image automate = new Image(getClass().getResource("/QueueAutomate.png").toExternalForm());
        gc.drawImage(automate, 200, 160, 40, 60);

        // Bank tellers with dynamic spacing
        Image teller = new Image(getClass().getResource("/Teller.png").toExternalForm());
        for(int i = 0; i < activeStations; i++) {
            int yPosition = margin + (int)(spacing * i);
            gc.drawImage(teller, tellerX, yPosition, tellerWidth, tellerHeight);
        }
        Image accountTeller = new Image(getClass().getResource("/accountTeller.png").toExternalForm());
        gc.drawImage(accountTeller, tellerX, canvasHeight - margin - tellerHeight, tellerWidth, tellerHeight);
    }

    private void drawCustomerQueues(Map<String, List<Integer>> queueStatus) {
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
        double canvasHeight = simulationCanvas.getHeight();
        int activeStations = controller.getNumberOfStations();

        double totalSpace = canvasHeight - 2 * margin - 60;
        double spacing = totalSpace / (activeStations - 1);

        // Draw automat queue
        List<Integer> automatQueue = queueStatus.get("automat");
        if (automatQueue != null) {
            drawQueueDots(gc, automatQueue, 150, 180, true);
        }

        // Draw teller queues starting at topMargin
        for(int i = 1; i <= activeStations; i++) {
            List<Integer> tellerQueue = queueStatus.get("teller" + i);
            if (tellerQueue != null) {
                int yPosition = margin + (int)(spacing * (i-1));
                drawQueueDots(gc, tellerQueue, 450, yPosition, true);
            }
        }

        // Draw account queue exactly at bottom margin
        List<Integer> accountQueue = queueStatus.get("account");
        if (accountQueue != null) {
            drawQueueDots(gc, accountQueue, 450, (int)(canvasHeight - margin - 50), true);
        }
    }


    private void drawCustomerDot(GraphicsContext gc, int clientSign, int x, int y) {
        gc.setFill(clientSign == 1 ? Color.BLUE : Color.RED);
        gc.fillOval(x, y, 10, 10);
    }

    private void drawQueueDots(GraphicsContext gc, List<Integer> clientSigns, int startX, int startY, boolean horizontal) {
        if (clientSigns == null) return;

        for (int i = 0; i < clientSigns.size(); i++) {
            int x = startX - (i * 20);
            int y = startY+10;
            drawCustomerDot(gc, clientSigns.get(i), x, y);
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
        statsPanel.getChildren().clear();
        statsPanel.getChildren().add(new Label(stats));
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
