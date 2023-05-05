package com.grzegorz2047.genetic.pathfinders;

import com.grzegorz2047.genetic.pathfinders.ga.GeneticAlgorithm;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    public static final double MUTATION_RATE = 0.001;
    public static final int MAX_GENERATION = 25000;
    public static final int POP_SIZE = 140;
    public static final int CHROMOSOME_LENGTH = 70;
    public static final int GENE_LENGTH = 2;
    private GeneticAlgorithm geneticAlgorithm;
    private Canvas canvas;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();
        ObservableList<Node> children = root.getChildren();
        children.add(canvas);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
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
        Text textElement = new Text((double) WINDOW_WIDTH /4,WINDOW_HEIGHT-10,"Wciśnij enter, aby rozpocząć symulację");
        textElement.setFont(Font.font(21));
        Pane e1 = new Pane();
        e1.getChildren().add((textElement));
        children.add(e1);
        primaryStage.show();


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
