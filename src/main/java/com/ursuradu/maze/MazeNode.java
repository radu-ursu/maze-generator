package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MazeNode {

    private BoardNode boardNode;
    private MazeNode parent;
    private List<MazeNode> children = new ArrayList<>();

    public MazeNode(BoardNode boardNode) {
        this.boardNode = boardNode;
    }

    public MazeNode(BoardNode boardNode, MazeNode parent) {
        this.boardNode = boardNode;
        this.parent = parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

}
