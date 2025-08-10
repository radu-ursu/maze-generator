package com.ursuradu.maze;

import java.util.List;
import java.util.Random;

public class RandomGenerator {
    static Random random = new Random();

    public static BoardNode getRandomNode(Board board) {
        int randomX = random.nextInt(board.getLAST_X_POSITION() + 1);
        int randomY = random.nextInt(board.getLAST_Y_POSITION() + 1);
        System.out.println("randomX: " + randomX + " randomY: " + randomY);
        return board.getNodes().get(new Position(randomX, randomY));
    }

    public static BoardNode getRandomEdgeNode(Board board) {
        BoardNode node;
        do {
            node = getRandomNode(board);
        }
        while (!board.isEdge(node));
        return node;
    }

    public static BoardNode getRandomNodeFrom(List<BoardNode> nodes) {
        return nodes.get(random.nextInt(nodes.size()));
    }
}
