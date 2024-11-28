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
        stationsSelector.getItems().addAll("3 Stations", "4 Stations", "5 Stations", "6 Stations", "7 Stations", "8 Stations");
        stationsSelector.setValue("3 Stations");
        intervalSlider.setShowTickLabels(true);
        intervalSlider.setShowTickMarks(true);
        intervalSlider.setMajorTickUnit(1);
        intervalSlider.setBlockIncrement(1);
        intervalSlider.setSnapToTicks(true);

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

        // Initial draw
        drawBaseElements();

        stage.show();

        startButton.setOnAction(e -> controller.startSimulation());
        pauseButton.setOnAction(e -> controller.pauseSimulation());
        resetButton.setOnAction(e -> controller.resetSimulation());

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setSimulationSpeed(newVal.doubleValue()));

        stationsSelector.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setNumberOfStations(Integer.parseInt(newVal.split(" ")[0])));

        intervalSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                controller.setArrivalInterval(newVal.doubleValue()));
    }

    public void updateQueueVisualization(Map<String, List<Customer>> queueStatus) {
        Platform.runLater(() -> {
            GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

            drawBaseElements();
            drawCustomerQueues(controller.convertQueueStatusToSigns(queueStatus));
        });
    }
    private void drawBaseElements() {
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();

        // Canvas with background color
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

        // Queue automat
        Image automate = new Image(getClass().getResource("/QueueAutomate.png").toExternalForm());
        gc.drawImage(automate, 100, 160, 40, 60);

        // Bank tellers
        Image teller = new Image(getClass().getResource("/Teller.png").toExternalForm());
        int tellerWidth = 50;
        int tellerHeight = 60;
        int tellerX = 500;
        int startY = 50;
        int spacing = 70;

        // Draw all 4 tellers
        for(int i = 0; i < 4; i++) {
            gc.drawImage(teller, tellerX, startY + (spacing * i), tellerWidth, tellerHeight);
        }
    }

    private void drawCustomerQueues(Map<String, List<Integer>> queueStatus) {
        System.out.println("Drawing queues with status: " + queueStatus);
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();

        List<Integer> automatQueue = queueStatus.get("automat");
        int startX = 50;
        int startY = 170;
        System.out.println("Automat queue: " + automatQueue);
        drawQueueDots(gc, automatQueue, startX, startY, true);

        for(int i = 1; i <= 3; i++) {
            List<Integer> tellerQueue = queueStatus.get("teller" + i);
            System.out.println("Teller " + i + " queue: " + tellerQueue);
            drawQueueDots(gc, tellerQueue, 450, 70 + (70 * (i-1)), true);
        }

        List<Integer> accountQueue = queueStatus.get("account");
        System.out.println("Account queue: " + accountQueue);
        drawQueueDots(gc, accountQueue, 450, 280, true);
    }


    private void drawCustomerDot(GraphicsContext gc, int clientSign, int x, int y) {
        gc.setFill(clientSign == 1 ? Color.BLUE : Color.RED);
        gc.fillOval(x, y, 10, 10);
    }

    private void drawQueueDots(GraphicsContext gc, List<Integer> clientSigns, int startX, int startY, boolean horizontal) {
        if (clientSigns == null) return;

        for (int i = 0; i < clientSigns.size(); i++) {
            int x = startX - (i * 20);
            int y = startY;
            drawCustomerDot(gc, clientSigns.get(i), x, y);
        }
    }



}
