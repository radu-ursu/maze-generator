package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MazeNode {

    protected Position position;
    protected MazeNode parent;
    protected List<MazeNode> children = new ArrayList<>();

    public MazeNode(MazeNode parent, Position position) {
        this.parent = parent;
        this.position = position;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public List<MazeNode> getChildren(MazeNodeOrientation orientation) {
        return children;
    }
}
