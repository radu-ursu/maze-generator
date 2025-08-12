package com.ursuradu.maze;

import java.util.List;
import java.util.Random;

public class RandomGenerator {
    static Random random = new Random();

    public static Position getRandomPosition(Maze maze) {
        int randomX = random.nextInt(maze.getWidth());
        int randomY = random.nextInt(maze.getHeight());
        System.out.println("randomX: " + randomX + " randomY: " + randomY);
        return new Position(randomX, randomY);
    }

    public static Position getRandomEdgeNode(Maze maze) {
        Position position;
        do {
            position = getRandomPosition(maze);
        }
        while (!maze.isEdge(position));
        return position;
    }

    public static Position getRandomPositionFrom(List<Position> positions) {
        return positions.get(random.nextInt(positions.size()));
    }

}
