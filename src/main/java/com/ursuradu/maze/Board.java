package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.ursuradu.maze.DIRECTION.*;

@Getter
@Setter
public class Board {

    public static final int FIRST_X_POSITION = 0;
    public static final int FIRST_Y_POSITION = 0;
    public int LAST_X_POSITION;
    public int LAST_Y_POSITION;

    Map<Position, BoardNode> nodes = new HashMap<>();

    public Board(int x, int y) {
        LAST_Y_POSITION = y - 1;
        LAST_X_POSITION = x - 1;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Position position = new Position(i, j);
                nodes.put(position, new BoardNode(position));
            }
        }
        nodes.values().forEach(this::setNeighbours);
    }

    private void setNeighbours(BoardNode node) {
        if (node.getX() != FIRST_X_POSITION) {
            node.addNeighbour(new NodeRelation(nodes.get(new Position(node.getX() - 1, node.getY())), LEFT));
        }
        if (node.getY() != FIRST_Y_POSITION) {
            node.addNeighbour(new NodeRelation(nodes.get(new Position(node.getX(), node.getY() - 1)), UP));
        }
        if (node.getX() != LAST_X_POSITION) {
            node.addNeighbour(new NodeRelation(nodes.get(new Position(node.getX() + 1, node.getY())), RIGHT));
        }
        if (node.getY() != LAST_Y_POSITION) {
            node.addNeighbour(new NodeRelation(nodes.get(new Position(node.getX(), node.getY() + 1)), DOWN));
        }
    }


    public boolean isEdge(BoardNode node) {
        return node.getX() == LAST_X_POSITION
                || node.getY() == LAST_Y_POSITION
                || node.getX() == FIRST_X_POSITION
                || node.getY() == FIRST_Y_POSITION;
    }
}
