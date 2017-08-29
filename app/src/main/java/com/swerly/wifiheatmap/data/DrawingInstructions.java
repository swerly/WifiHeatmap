/*
 * Copyright (c) 2017 Seth Werly.
 *
 * This file is part of WifiHeatmap.
 *
 *     WifiHeatmap is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     WifiHeatmap is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with WifiHeatmap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.swerly.wifiheatmap.data;

/**
 * Created by Seth on 8/17/2017.
 */

public class DrawingInstructions{
    //FIRST ELEMENT IS NUMBER OF ROWS
    //OTHER ELEMENTS ARE DISTANCE FROM 0, AND HOW MANY SQUARES TO DRAW

    public static int[][] CIRCLE_ONE = {
            {17,0},
            {4,4},
            {7,7},
            {9,9},
            {10,10},
            {11,11},
            {12,12},
            {13,13},
            {14,14},
            {15,15},
            {15,15},
            {16,16},
            {16,16},
            {16,16},
            {17,17},
            {17,17},
            {17,17},
            {17,17}
    };

    public static int[][] CIRCLE_TWO = {
            {21, 0},
            {5, 5},
            {8, 8},
            {10, 10},
            {12, 12},
            {13, 9},
            {14, 7},
            {15, 6},
            {16, 6},
            {17, 6},
            {18, 6},
            {18, 5},
            {19, 5},
            {19, 4},
            {20, 5},
            {20, 4},
            {20, 4},
            {21, 5},
            {21, 4},
            {21, 4},
            {21, 4},
            {21, 4}
    };

    public static int[][] CIRCLE_THREE = {
            {25,0},
            {5,5},
            {9,9},
            {11,11},
            {13,13},
            {14,9},
            {16,8},
            {17,7},
            {18,6},
            {19,6},
            {20,6},
            {20,5},
            {21,5},
            {22,5},
            {22,4},
            {23,5},
            {23,4},
            {24,5},
            {24,4},
            {24,4},
            {24,4},
            {25,4},
            {25,4},
            {25,4},
            {25,4},
            {25,4}
    };
}
