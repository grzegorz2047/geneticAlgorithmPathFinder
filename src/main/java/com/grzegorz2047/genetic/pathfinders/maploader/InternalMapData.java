package com.grzegorz2047.genetic.pathfinders.maploader;

import com.grzegorz2047.genetic.pathfinders.MapData;
import com.grzegorz2047.genetic.pathfinders.ga.ExperimentMap;

public class InternalMapData implements MapData {

    private static final int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
            {1, 0, ExperimentMap.END_POSITION_ID, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, ExperimentMap.START_POSITION_ID},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    @Override
    public int[][] getMap() {
        return map;
    }

    @Override
    public int getMapWidth() {
        return map[0].length;
    }

    @Override
    public int getMapHeight() {
        return map.length;
    }
}
