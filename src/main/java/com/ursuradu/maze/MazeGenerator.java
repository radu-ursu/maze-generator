package com.ursuradu.maze;

import java.util.List;

public class MazeGenerator {

    //    static MazeNode lastAddedNode;
    static Maze maze;

    public static Maze generateMaze(Board board) {

        maze = new Maze(board);
        BoardNode startBoardNode = RandomGenerator.getRandomEdgeNode(board);

        MazeNode root = new MazeNode(startBoardNode);
        maze.setRoot(root);
        addNodeToMaze(root, null);

        while (!maze.isComplete()) {
            addNextNode(root);
        }

        return maze;
    }

    private static void addNextNode(MazeNode justAddedNode) {
        BoardNode randomFreeNeighbour;
        do {
            randomFreeNeighbour = getRandomFreeNeighbour(justAddedNode);
            if (randomFreeNeighbour != null) {
                MazeNode newMazeNode = new MazeNode(randomFreeNeighbour);
                addNodeToMaze(newMazeNode, justAddedNode);
                addNextNode(newMazeNode);
            }
        } while (randomFreeNeighbour != null);


    }

    private static BoardNode getRandomFreeNeighbour(MazeNode fromNode) {
        List<BoardNode> freeBoardNeighbours = maze.getFreeBoardNeighboursOf(fromNode);
        if (freeBoardNeighbours.isEmpty()) {
            return null;
        }
        return RandomGenerator.getRandomNodeFrom(freeBoardNeighbours);
    }

    private static void addNodeToMaze(MazeNode mazeNode, MazeNode parent) {
        if (parent != null) {
            System.out.println("Adding node " + mazeNode.getBoardNode().getPosition() + " to parent " + parent.getBoardNode().getPosition());
        }
        maze.addNodeToMaze(mazeNode);
        if (parent != null) {
            parent.getChildren().add(mazeNode);
            mazeNode.setParent(parent);

        }
//        lastAddedNode = mazeNode;
    }


}
