package com.ursuradu.maze;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MazePath {

    private int junctions;
    private List<MazeNode> nodes;
    private Map<Integer, Portal> portals = new HashMap<>();

    public MazePath(int junctions, List<MazeNode> nodes) {
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
