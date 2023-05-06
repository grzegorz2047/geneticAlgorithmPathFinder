package com.grzegorz2047.genetic.pathfinders;

import com.grzegorz2047.genetic.pathfinders.ga.ExperimentMap;
import com.grzegorz2047.genetic.pathfinders.ga.GeneticAlgorithm;
import com.grzegorz2047.genetic.pathfinders.maploader.ExternalMapData;
import com.grzegorz2047.genetic.pathfinders.maploader.InternalMapData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/*
    Kod bazowany na kodzie autora Mat Buckland z książki "Ai Techniques For Game Programming"
 */


public class HelloApplication extends Application {

    private static final String APPLICATION_NAME = "PathFinder";
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;


    private static final int CANVAS_WIDTH = 450;
    private static final int CANVAS_HEIGHT = 300;

    public static final double CROSSOVER_RATE = 0.7;
    public static double MUTATION_RATE = 0.01;
    public static final int MAX_GENERATION = 25000;
    public static final int POP_SIZE = 170;
    public static int CHROMOSOME_LENGTH = 70;//Musi być wartość parzysta (dwa bity na kierunek)
    public static final int GENE_LENGTH = 2;
    private GeneticAlgorithm geneticAlgorithm;
    private Canvas canvas;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        String filePath = "config.properties";

        // Sprawdzanie, czy plik istnieje
        if (!Files.exists(Paths.get(filePath))) {
            createFile(filePath);
        } else {
            System.out.println("Plik properties istnieje. Wczytywanie danych...");
            // Wczytywanie istniejącego pliku properties
            Properties properties = new Properties();
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                properties.load(inputStream);

                CHROMOSOME_LENGTH = Integer.parseInt(properties.getProperty("CHROMOSOME_LENGTH"));
                MUTATION_RATE = Double.parseDouble(properties.getProperty("MUTATION_RATE"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        launch(args);
    }

    private static void createFile(String filePath) {
        Properties properties = new Properties();

        // Dodaj pary klucz-wartość do obiektu Properties
        properties.setProperty("CHROMOSOME_LENGTH", String.valueOf(CHROMOSOME_LENGTH));
        properties.setProperty("MUTATION_RATE", String.valueOf(MUTATION_RATE));

        // Zapisz obiekt Properties do pliku
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, "Database configuration");
            System.out.println("Plik properties został utworzony i zapisany.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();
        ObservableList<Node> children = root.getChildren();
        children.add(canvas);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        ExperimentMap.loadMap(new InternalMapData());
        geneticAlgorithm = initNewAlgorithm();
        Platform.runLater(() -> render(gc, primaryStage));
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> {
                    new Thread(() -> {
                        geneticAlgorithm.run();
                        while (true) {
                            if (running.get()) {
                                geneticAlgorithm.epoch();
                                render(gc, primaryStage);
                                running.set(geneticAlgorithm.isRunning());
                            }

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    running.set(true);
                }
                case ESCAPE -> primaryStage.close();
                case SPACE -> running.set(!running.get());
            }
        });

        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        Text textElement = new Text((double) WINDOW_WIDTH / 4, WINDOW_HEIGHT - 10, "Wciśnij enter, aby rozpocząć symulację");
        textElement.setFont(Font.font(21));
        Pane e1 = new Pane();
        e1.getChildren().add((textElement));
        children.add(e1);

        addMenu(root, primaryStage);
        primaryStage.show();


    }

    private void addMenu(StackPane root, Stage primaryStage) {
        Menu menu = new Menu("Menu");
        MenuItem loadMapMenuItem = new MenuItem("Load Map");
        menu.getItems().add(loadMapMenuItem);
        loadMapMenuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Map");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                System.out.println("Selected file: " + file.getAbsolutePath());
                try {
                    ExperimentMap.loadMap(new ExternalMapData(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                render(canvas.getGraphicsContext2D(), primaryStage);
            }
        });
        // Utwórz menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        VBox vbox = new VBox(menuBar);
        vbox.setPadding(new Insets(10));
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vbox);
        root.getChildren().add(borderPane);
    }

    private GeneticAlgorithm initNewAlgorithm() {
        return new GeneticAlgorithm(CROSSOVER_RATE,
                MUTATION_RATE,
                POP_SIZE,
                CHROMOSOME_LENGTH,
                GENE_LENGTH,
                MAX_GENERATION);
    }

    private void render(GraphicsContext gc, Stage primaryStage) {
        Platform.runLater(() -> geneticAlgorithm.render(canvas.getWidth(), canvas.getHeight(), gc, primaryStage));

    }


}
