package com.ursuradu.maze.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MazePath {

    private int junctions;
    private List<MazeNode> nodes;

    public MazePath(final int junctions, final List<MazeNode> nodes) {
        this.junctions = junctions;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "MazePath{" +
                "size=" + nodes.size() +
                ", junctions=" + junctions +
                ", nodes=" + nodes +
                '}';
    }
}
