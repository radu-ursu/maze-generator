package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MazePath {

    private int junctions;
    private List<MazeNode> nodes;
    private boolean containsPortals;

    public MazePath(int junctions, List<MazeNode> nodes) {
        this.junctions = junctions;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "MazePath{" +
                "size=" + nodes.size() +
                ", containsPortals=" + containsPortals +
                ", junctions=" + junctions +
                ", nodes=" + nodes +
                '}';
    }
}
