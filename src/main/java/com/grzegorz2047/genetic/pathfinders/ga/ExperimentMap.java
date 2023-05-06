package com.grzegorz2047.genetic.pathfinders.ga;

import com.grzegorz2047.genetic.pathfinders.maploader.InternalMapData;
import com.grzegorz2047.genetic.pathfinders.MapData;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class ExperimentMap {


    public static final int OBSTACLE = 1;
    public static final int END_POSITION_ID = 8;
    public static final int START_POSITION_ID = 5;


    private static int startX;
    private static int startY;
    private static int endX;
    private static int endY;

    private final int[][] memoryPath;
    private final int NORTH = 0;
    private final int SOUTH = 1;
    private final int EAST = 2;
    private final int WEST = 3;
    private final int initialSquareSize = 20;

    static {
        map = new InternalMapData();
        initStartAndEndPos();
    }

    private static MapData map;

    public ExperimentMap() {
        memoryPath = new int[map.getMapHeight()][map.getMapWidth()];
        resetMemory();

    }

    private static void initStartAndEndPos() {
        for (int y = 0; y < map.getMap().length; y++) {
            for (int x = 0; x < map.getMap()[y].length; x++) {
                int cellType = map.getMap()[y][x];
                if (cellType == END_POSITION_ID) {
                    endX = x;
                    endY = y;
                }
                if (cellType == START_POSITION_ID) {
                    startX = x;
                    startY = y;
                }
            }
        }
    }

    public ExperimentMap(int[][] cBobsMap) {
        initStartAndEndPos();
        memoryPath = cBobsMap;
    }

    public static void loadMap(MapData mapData) {
        map = mapData;
        initStartAndEndPos();
    }

    public void renderMap(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        int blockSizeX = (canvasWidth - 2 * initialSquareSize) / map.getMapWidth();
        int blockSizeY = (canvasHeight - 2 * initialSquareSize) / map.getMapHeight();

        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                int left = initialSquareSize + (blockSizeX * x);
                int top = initialSquareSize + (blockSizeY * y);

                int mapCell = map.getMap()[y][x];
                switch (mapCell) {
                    case 1 -> {
                        gc.setFill(Color.BLACK);
                        gc.fillRect(left, top, blockSizeX, blockSizeY);
                    }
                    case START_POSITION_ID -> {
                        gc.setFill(Color.GREEN);
                        gc.fillRect(left, top, blockSizeX, blockSizeY);
                    }
                    case END_POSITION_ID -> {
                        gc.setFill(Color.RED);
                        gc.fillRect(left, top, blockSizeX, blockSizeY);
                    }
                }
            }
        }
    }

    public void memoryRenderPath(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        int adjustedSquareSizeX = (canvasWidth - 2 * initialSquareSize) / map.getMapWidth();
        int adjustedSquareSizeY = (canvasHeight - 2 * initialSquareSize) / map.getMapHeight();

        gc.setFill(Color.LIGHTGRAY);
        int countPathTracks = 0;
        for (int posY = 0; posY < map.getMapHeight(); posY++) {
            for (int posX = 0; posX < map.getMapWidth(); posX++) {
                if (memoryPath[posY][posX] == 1) {
                    int left = initialSquareSize + (adjustedSquareSizeX * posX);
                    int top = initialSquareSize + (adjustedSquareSizeY * posY);
                    gc.fillRect(left, top, adjustedSquareSizeX, adjustedSquareSizeY);
                    countPathTracks++;
                }
            }
        }
        //System.out.println(countPathTracks);
    }

    public double calculateFitness(List<Integer> currentPath) {
        int posX = startX;
        int posY = startY;
        for (int direction : currentPath) {
            switch (direction) {
                case NORTH -> {
                    boolean isOutOfBounds = posY - 1 < 0;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextUpPositionCell(posX, posY))) {
                        posY -= 1;
                    }
                }
                case SOUTH -> {
                    boolean isOutOfBounds = posY + 1 >= map.getMapHeight();
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextDownPositionCell(posX, posY))) {
                        posY += 1;
                    }
                }
                case EAST -> {
                    boolean isOutOfBounds = posX + 1 >= map.getMapWidth();
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextLeftPositionCell(posX, posY))) {
                        posX += 1;
                    }
                }
                case WEST -> {
                    boolean isOutOfBounds = posX - 1 < 0;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextRightPositionCell(posX, posY))) {
                        posX -= 1;
                    }
                }
            }
        }

        int diffX = Math.abs(posX - endX);
        int diffY = Math.abs(posY - endY);

        return 1.0 / (double) (diffX + diffY + 1);
    }
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
    private int getNextUpPositionCell(int posX, int posY) {
        return map.getMap()[posY - 1][posX];
    }

    private int getNextDownPositionCell(int posX, int posY) {
        return map.getMap()[posY + 1][posX];
    }

    private int getNextLeftPositionCell(int posX, int posY) {
        return map.getMap()[posY][posX + 1];
    }

    private int getNextRightPositionCell(int posX, int posY) {
        return map.getMap()[posY][posX - 1];
    }

    private boolean canMove(int nextRightPositionCell) {
        return nextRightPositionCell != 1;
    }

    public int[][] copyOfPath(List<Integer> currentPath, ExperimentMap tempMemory) {
        int posX = startX;
        int posY = startY;
        int[][] clone = tempMemory.memoryPath.clone();
        for (int direction : currentPath) {
            switch (direction) {
                case NORTH -> {
                    boolean isOutOfBounds = posY - 1 < 0;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextUpPositionCell(posX, posY))) {
                        posY -= 1;
                    }
                }
                case SOUTH -> {
                    boolean isOutOfBounds = posY + 1 >= map.getMapHeight();
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextDownPositionCell(posX, posY))) {
                        posY += 1;
                    }
                }
                case EAST -> {
                    boolean isOutOfBounds = posX + 1 >= map.getMapWidth();
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextLeftPositionCell(posX, posY))) {
                        posX += 1;
                    }
                }
                case WEST -> {
                    boolean isOutOfBounds = posX - 1 < 0;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextRightPositionCell(posX, posY))) {
                        posX -= 1;
                    }
                }
            }
            clone[posY][posX] = 1;
        }


        return clone;
    }


    public void resetMemory() {
        for (int y = 0; y < map.getMapHeight(); y++) {
            for (int x = 0; x < map.getMapWidth(); x++) {
                memoryPath[y][x] = 0;
            }
        }
    }

}

