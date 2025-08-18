package com.ursuradu.maze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MazeGenerator {

    static MazeNode lastAddedNode;
    static Maze maze;
    static Map<Position, MazeNode> nonFinalMazeNodes = new HashMap<>();

    static MazeNode currentBridgeNode;
    static Direction currentBridgeDirection;

    public static Maze generateMaze(MazeConfig mazeConfig) {

        maze = new Maze(mazeConfig);
        Position startPosition = RandomGenerator.getRandomEdgeNode(maze);

        MazeNode root = new MazeNode(null, startPosition);
        maze.setRoot(root);
        addNodeToMaze(root, null);

        while (!nonFinalMazeNodes.isEmpty()) {
            addNextNode();
        }

        return maze;
    }

    private static void addNextNode() {
        MazeNode nodeToStartFrom = getNodeToStartFrom();
        Position nextFreePosition = null;
        if (currentBridgeDirection != null) {
            Optional<Position> nextPosition = maze.getPositionFrom(currentBridgeNode.getPosition(), currentBridgeDirection);
            if (nextPosition.isEmpty()) {
                throw new RuntimeException("Something bad happened, this should have been checked");
            } else {
                if (maze.nodesByPosition.containsKey(nextPosition.get())) {

                } else {
                    // found free position, ending bridge making
                    nextFreePosition = nextPosition.get();
                    currentBridgeDirection = null;
                    currentBridgeNode = null;
                }

            }
        } else {
            nextFreePosition = getRandomFreeNeighbour(nodeToStartFrom);
        }
        if (nextFreePosition != null) {
            boolean startingBridge = maze.nodesByPosition.containsKey(nextFreePosition);
            if (startingBridge) {
                System.out.println("Creating bridge on position " + nextFreePosition);
                MazeNode nodeThatWillBeABridge = maze.nodesByPosition.get(nextFreePosition);
                nonFinalMazeNodes.remove(nodeThatWillBeABridge.getPosition());
                currentBridgeNode = nodeThatWillBeABridge;

                currentBridgeDirection = maze.getMovementDirection(nodeToStartFrom.getPosition(), nodeThatWillBeABridge.getPosition());
            } else {
                MazeNode newMazeNode = new MazeNode(nodeToStartFrom, nextFreePosition);
                addNodeToMaze(newMazeNode, nodeToStartFrom);
            }
        } else {
            System.out.println("Marking as final " + nodeToStartFrom.getPosition());
            nonFinalMazeNodes.remove(nodeToStartFrom.getPosition());
        }
    }

    private static MazeNode getNodeToStartFrom() {
        if (currentBridgeNode != null) {
            return currentBridgeNode;
        }
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
        List<Position> freeBoardNeighbours =
                maze.getMazeConfig().hasBridges() ? maze.getFreeNearbyPositionsWithBridges(fromNode) :
                        maze.getFreeNearbyPositionsNoBridges(fromNode);
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
