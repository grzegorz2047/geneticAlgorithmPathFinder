package com.grzegorz2047.genetic.pathfinders.ga;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class ExperimentMap {
    private static final int MAP_HEIGHT = 10;
    private static final int MAP_WIDTH = 15;
    private static final int END_POSITION = 8;
    private static final int START_POSITION = 5;
    private static final int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
            {END_POSITION, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, START_POSITION},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private static final int startX = 14;
    private static final int startY = 7;
    private static final int endX = 0;
    private static final int endY = 2;

    private final int[][] memoryPath;
    private final int NORTH = 0;
    private final int SOUTH = 1;
    private final int EAST = 2;
    private final int WEST = 3;
    private final int initialSquareSize = 20;


    public ExperimentMap() {
        memoryPath = new int[MAP_HEIGHT][MAP_WIDTH];
        resetMemory();
    }

    public ExperimentMap(int[][] cBobsMap) {
        memoryPath = cBobsMap;
    }

    public void renderMap(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        int blockSizeX = (canvasWidth - 2 * initialSquareSize) / MAP_WIDTH;
        int blockSizeY = (canvasHeight - 2 * initialSquareSize) / MAP_HEIGHT;

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int left = initialSquareSize + (blockSizeX * x);
                int top = initialSquareSize + (blockSizeY * y);

                int mapCell = map[y][x];
                switch (mapCell) {
                    case 1 -> {
                        gc.setFill(Color.BLACK);
                        gc.fillRect(left, top, blockSizeX, blockSizeY);
                    }
                    case START_POSITION, END_POSITION -> {
                        gc.setFill(Color.RED);
                        gc.fillRect(left, top, blockSizeX, blockSizeY);
                    }
                }
            }
        }
    }

    public void memoryRenderPath(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        int adjustedSquareSizeX = (canvasWidth - 2 * initialSquareSize) / MAP_WIDTH;
        int adjustedSquareSizeY = (canvasHeight - 2 * initialSquareSize) / MAP_HEIGHT;

        gc.setFill(Color.LIGHTGRAY);
        int countPathTracks = 0;
        for (int posY = 0; posY < MAP_HEIGHT; posY++) {
            for (int posX = 0; posX < MAP_WIDTH; posX++) {
                if (memoryPath[posY][posX] == 1) {
                    int left = initialSquareSize + (adjustedSquareSizeX * posX);
                    int top = initialSquareSize + (adjustedSquareSizeY * posY);
                    gc.fillRect(left, top, adjustedSquareSizeX, adjustedSquareSizeY);
                    countPathTracks++;
                }
            }
        }
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
                    boolean isOutOfBounds = posY + 1 >= MAP_HEIGHT;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextDownPositionCell(posX, posY))) {
                        posY += 1;
                    }
                }
                case EAST -> {
                    boolean isOutOfBounds = posX + 1 >= MAP_WIDTH;
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

    private static int getNextUpPositionCell(int posX, int posY) {
        return map[posY - 1][posX];
    }

    private static int getNextDownPositionCell(int posX, int posY) {
        return map[posY + 1][posX];
    }

    private static int getNextLeftPositionCell(int posX, int posY) {
        return map[posY][posX + 1];
    }

    private static int getNextRightPositionCell(int posX, int posY) {
        return map[posY][posX - 1];
    }

    private static boolean canMove(int nextRightPositionCell) {
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
                    boolean isOutOfBounds = posY + 1 >= MAP_HEIGHT;
                    if (isOutOfBounds) {
                        continue;
                    }
                    if (canMove(getNextDownPositionCell(posX, posY))) {
                        posY += 1;
                    }
                }
                case EAST -> {
                    boolean isOutOfBounds = posX + 1 >= MAP_WIDTH;
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
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                memoryPath[y][x] = 0;
            }
        }
    }

}

