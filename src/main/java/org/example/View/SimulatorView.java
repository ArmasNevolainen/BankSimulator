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
import java.util.Objects;

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
        Image automate = new Image(getClass().getResource("/QueueAutomate.png").toExternalForm());
        Image teller = new Image(getClass().getResource("/Teller.png").toExternalForm());
        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());
        ImageView imageAutomate = new ImageView(automate);
        imageAutomate.setFitWidth(40);
        imageAutomate.setFitHeight(60);
        gc.drawImage(automate, 100, 160, 40, 60);
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

// Draw 3 teller stations
        int tellerWidth = 50;
        int tellerHeight = 60;
        int tellerX = 550;    // X position for all tellers
        int startY = 50;     // Starting Y position for first teller
        int spacing = 120;    // Vertical space between tellers

        gc.drawImage(teller, tellerX, startY, tellerWidth, tellerHeight);
        gc.drawImage(teller, tellerX, startY + spacing, tellerWidth, tellerHeight);
        gc.drawImage(teller, tellerX, startY + spacing * 2, tellerWidth, tellerHeight);



        // Status Area
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

//    private void drawCustomers(List<Customer> customers) {
//        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
//        Image customerImage = new Image(getClass().getResource("/Customer.png").toExternalForm());
//
//        // Clear previous drawings
//        gc.clearRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

//        // Draw each customer
//        for (Customer customer : customers) {
//            // Calculate position based on queue position
//            int x = 150 + (customer.getQueuePosition() * 30);
//            int y = 160;
//
//            // Draw customer
//            gc.drawImage(customerImage, x, y, 20, 40);

//        }
//    }

//    private void redrawSimulation() {
//        GraphicsContext gc = simulationCanvas.getGraphicsContext2D();
//        gc.setFill(Color.GRAY);
//        gc.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());
//    }




}
