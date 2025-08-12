package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MazeNode {

    private Position position;
    private MazeNode parent;
    private List<MazeNode> children = new ArrayList<>();

    public MazeNode(MazeNode parent, Position position) {
        this.parent = parent;
        this.position = position;
    }

    public boolean isRoot() {
        return parent == null;
    }

}
