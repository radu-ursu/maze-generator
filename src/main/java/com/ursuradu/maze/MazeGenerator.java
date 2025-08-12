package com.ursuradu.maze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazeGenerator {

    static MazeNode lastAddedNode;
    static Maze maze;
    static Map<Position, MazeNode> nonFinalMazeNodes = new HashMap<>();

    public static Maze generateMaze(int maxX, int maxY) {

        maze = new Maze(maxX, maxY);
        Position startPosition = RandomGenerator.getRandomEdgeNode(maze);

        MazeNode root = new MazeNode(null, startPosition);
        maze.setRoot(root);
        addNodeToMaze(root, null);

        while (!maze.isComplete()) {
            addNextNode();
        }

        return maze;
    }

    private static void addNextNode() {
        MazeNode nodeToStartFrom = getNodeToStartFrom();
        Position randomFreeNeighbourPosition = getRandomFreeNeighbour(nodeToStartFrom);
        if (randomFreeNeighbourPosition != null) {
            MazeNode newMazeNode = new MazeNode(nodeToStartFrom, randomFreeNeighbourPosition);
            addNodeToMaze(newMazeNode, nodeToStartFrom);
        } else {
            System.out.println("Marking as final " + nodeToStartFrom.getPosition());
            nonFinalMazeNodes.remove(nodeToStartFrom.getPosition());
        }
    }

    private static MazeNode getNodeToStartFrom() {
        if (nonFinalMazeNodes.containsKey(lastAddedNode.getPosition())) {
            System.out.println("Looking at lastAddedNode " + lastAddedNode.getPosition());
            return lastAddedNode;
        } else {
            Position randomNonFinalPosition = RandomGenerator.getRandomPositionFrom(nonFinalMazeNodes.keySet().stream().toList());
            System.out.println("Looking at nonFinalPosition " + randomNonFinalPosition);
            return nonFinalMazeNodes.get(randomNonFinalPosition);
        }
    }

    private static Position getRandomFreeNeighbour(MazeNode fromNode) {
        List<Position> freeBoardNeighbours = maze.getFreeNearbyPositions(fromNode);
        if (freeBoardNeighbours.isEmpty()) {
            return null;
        }
        return RandomGenerator.getRandomPositionFrom(freeBoardNeighbours);
    }

    private static void addNodeToMaze(MazeNode mazeNode, MazeNode parent) {
        if (parent != null) {
            System.out.println("Adding node " + mazeNode.getPosition() + " to parent " + parent.getPosition());
        }
        maze.addNodeToMaze(mazeNode);
        if (parent != null) {
            parent.getChildren().add(mazeNode);
            mazeNode.setParent(parent);
        }
        lastAddedNode = mazeNode;
        nonFinalMazeNodes.put(mazeNode.getPosition(), mazeNode);
    }


}
