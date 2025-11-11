package com.ursuradu.maze.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MazePath {

  private int junctions;
  private List<MazeNode> nodes;
  private Map<Integer, Portal> portals = new HashMap<>();

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
