package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Maze {

    Map<Position, MazeNode> nodesByPosition = new HashMap<>();
    private int width;
    private int height;
    private MazeNode root;


    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addNodeToMaze(MazeNode root) {
        nodesByPosition.put(root.getPosition(), root);
    }

    public boolean isComplete() {
        return nodesByPosition.size() == width * height;
    }

    public boolean isEdge(Position position) {
        return position.x() == 0 || position.x() == width - 1 || position.y() == 0 || position.y() == height - 1;
    }

    public List<Position> getFreeNearbyPositions(MazeNode mazeNode) {
        List<Position> result = new ArrayList<>();
        List<Position> all = getNearbyPositions(mazeNode);
        for (Position neighbourPosition : all) {
            if (!nodesByPosition.containsKey(neighbourPosition)) {
                result.add(neighbourPosition);
            }
        }
        return result;
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
