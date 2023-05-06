package com.grzegorz2047.genetic.pathfinders.maploader;

import com.grzegorz2047.genetic.pathfinders.MapData;
import com.grzegorz2047.genetic.pathfinders.ga.ExperimentMap;

import java.io.File;
import java.io.FileWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ExternalMapData implements MapData {

    private final int[][] map;
    private final int width;
    private final int height;

    public ExternalMapData(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        this.width = img.getWidth();
        this.height = img.getHeight();
        map = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Retrieving contents of a pixel
                int pixel = img.getRGB(x, y);
                //Creating a Color object from pixel value
                Color color = new Color(pixel, false);
                if (color.equals(Color.BLACK)) {
                    map[y][x] = ExperimentMap.OBSTACLE;
                }
                else if (color.equals(Color.RED)) {
                    map[y][x] = ExperimentMap.END_POSITION_ID;
                }
                else if (color.equals(Color.GREEN)) {
                    map[y][x] = ExperimentMap.START_POSITION_ID;
                }
                else {
                    map[y][x] = 0;
                }
            }
        }
    }

    public int[][] getMap() {
        return map;
    }

    @Override
    public int getMapWidth() {
        return width;
    }

    @Override
    public int getMapHeight() {
        return height;
    }
}
