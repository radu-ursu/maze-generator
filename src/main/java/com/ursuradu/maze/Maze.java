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
    private Board board;
    private MazeNode root;


    public Maze(Board board) {
        this.board = board;
    }

    public void addNodeToMaze(MazeNode root) {
        nodesByPosition.put(root.getBoardNode().getPosition(), root);
    }

    public boolean isComplete() {
        return nodesByPosition.size() == board.getNodes().size();
    }

    public List<BoardNode> getFreeBoardNeighboursOf(MazeNode mazeNode) {
        List<BoardNode> result = new ArrayList<>();
        List<NodeRelation> relations = mazeNode.getBoardNode().getRelations();
        for (NodeRelation relation : relations) {
            Position neighbourPosition = relation.neighbour().getPosition();
            if (!nodesByPosition.containsKey(neighbourPosition)) {
                result.add(relation.neighbour());
            }
        }
        return result;
    }
}
