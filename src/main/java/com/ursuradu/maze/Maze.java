package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.ursuradu.maze.Direction.*;
import static com.ursuradu.maze.MazeNodeOrientation.HORIZONTAL;
import static com.ursuradu.maze.MazeNodeOrientation.VERTICAL;

@Getter
@Setter
public class Maze {

    Map<Position, MazeNode> nodesByPosition = new HashMap<>();
    private int width;
    private int height;
    private MazeNode root;
    private MazeConfig mazeConfig;

    public Maze(MazeConfig mazeConfig) {
        this.width = mazeConfig.width();
        this.height = mazeConfig.height();
        this.mazeConfig = mazeConfig;
    }

    public void addNodeToMaze(MazeNode root) {
        nodesByPosition.put(root.getPosition(), root);
    }

    public boolean isEdge(Position position) {
        return position.x() == 0 || position.x() == width - 1 || position.y() == 0 || position.y() == height - 1;
    }

    public List<Position> getFreeNearbyPositionsNoBridges(MazeNode mazeNode) {
        List<Position> result = new ArrayList<>();
        List<Position> all = getNearbyPositions(mazeNode);
        for (Position neighbourPosition : all) {
            if (!nodesByPosition.containsKey(neighbourPosition)) {
                result.add(neighbourPosition);
            }
        }
        return result;
    }

    public List<Position> getFreeNearbyPositionsWithBridges(MazeNode mazeNode) {
        List<Position> result = new ArrayList<>();
        List<Position> all = getNearbyPositions(mazeNode);
        for (Position neighbourPosition : all) {
            if (!nodesByPosition.containsKey(neighbourPosition) || canMakeBridge(mazeNode, neighbourPosition)) {
                result.add(neighbourPosition);
            }
        }
        return result;
    }

    private boolean canMakeBridge(MazeNode mazeNode, Position neighbourPosition) {
        Direction movementDirection = getMovementDirection(mazeNode.getPosition(), neighbourPosition);
        Position currentPosition = mazeNode.getPosition();
        while (true) {
            Optional<Position> nextPosition = getPositionFrom(currentPosition, movementDirection);
            if (nextPosition.isPresent()) {
                MazeNode nodeAtPosition = nodesByPosition.get(nextPosition.get());
                if (nodeAtPosition == null) {
                    return true;
                } else {
                    MazeNodeOrientation orientation = getOrientation(nodeAtPosition);
                    if (orientation.equals(HORIZONTAL) && (movementDirection.equals(UP) || movementDirection.equals(DOWN))) {
                        currentPosition = nextPosition.get();
                    }
                    if (orientation.equals(VERTICAL) && (movementDirection.equals(LEFT) || movementDirection.equals(RIGHT))) {
                        currentPosition = nextPosition.get();
                    }
                }
            } else {
                return false;
            }
        }
    }

    private MazeNodeOrientation getOrientation(MazeNode mazeNode) {
        Set<Direction> directionsToLinks = getDirectionsToLinks(mazeNode);
        if (directionsToLinks.equals(Set.of(Direction.UP, Direction.DOWN))) {
            return MazeNodeOrientation.VERTICAL;
        } else if (directionsToLinks.equals(Set.of(LEFT, Direction.RIGHT))) {
            return HORIZONTAL;
        } else return MazeNodeOrientation.MULTIPLE;
    }


    public Set<Direction> getDirectionsToLinks(MazeNode node) {
        if (node instanceof BridgeMazeNode) {
            return null;
        }
        Set<Direction> directions = new HashSet<>();
        node.getChildren().forEach(child -> directions.add(getMovementDirection(node.getPosition(), child.getPosition())));
        if (node.getParent() != null) {
            directions.add(getMovementDirection(node.getPosition(), node.getParent().getPosition()));
        }
        return directions;
    }

    public Direction getMovementDirection(Position position, Position neighbourPosition) {
        if (position.x() < neighbourPosition.x()) {
            return RIGHT;
        }
        if (position.x() > neighbourPosition.x()) {
            return LEFT;
        }
        if (position.y() < neighbourPosition.y()) {
            return DOWN;
        }
        if (position.y() > neighbourPosition.y()) {
            return UP;
        }
        throw new IllegalArgumentException("Invalid movement direction");
    }


    public List<Position> getNearbyPositions(MazeNode node) {
        List<Position> result = new ArrayList<>();
        if (node.getPosition().x() != 0) {
            result.add(new Position(node.getPosition().x() - 1, node.getPosition().y()));
        }
        if (node.getPosition().y() != 0) {
            result.add(new Position(node.getPosition().x(), node.getPosition().y() - 1));
        }
        if (node.getPosition().x() != width - 1) {
            result.add(new Position(node.getPosition().x() + 1, node.getPosition().y()));
        }
        if (node.getPosition().y() != height - 1) {
            result.add(new Position(node.getPosition().x(), node.getPosition().y() + 1));
        }
        return result;
    }


}
