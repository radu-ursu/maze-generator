package com.ursuradu.maze;

import java.util.*;

import static com.ursuradu.maze.Direction.*;

public class Board {

    private Map<Position, List<MazeNode>> mazeMap = new HashMap<>();

    public Board(int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mazeMap.put(new Position(x, y), new ArrayList<>());
            }
        }
    }

    public List<MazeNode> getMazeNodesAtPosition(Position position) {
        return mazeMap.get(position);
    }

    public boolean isPositionFree(Position position) {
        return mazeMap.get(position).isEmpty();
    }

    public boolean isOutOfBounds(Position position) {
        return mazeMap.containsKey(position);
    }

    public Optional<Position> getPositionFrom(Position position, Direction direction) {
        Position newPosition;
        if (direction == DOWN) {
            newPosition = new Position(position.x(), position.y() + 1);
        } else if (direction == UP) {
            newPosition = new Position(position.x(), position.y() - 1);
        } else if (direction == LEFT) {
            newPosition = new Position(position.x() - 1, position.y());
        } else if (direction == RIGHT) {
            newPosition = new Position(position.x() + 1, position.y());
        } else throw new IllegalArgumentException("Invalid direction " + direction);

        return !isOutOfBounds(newPosition) ? Optional.of(newPosition) : Optional.empty();
    }

    public boolean isEdge(Position position) {
        return getPositionFrom(position, DOWN).isEmpty()
                || getPositionFrom(position, UP).isEmpty()
                || getPositionFrom(position, LEFT).isEmpty()
                || getPositionFrom(position, RIGHT).isEmpty();
    }
}
