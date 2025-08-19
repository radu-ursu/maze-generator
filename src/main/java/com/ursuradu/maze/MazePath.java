package com.ursuradu.maze;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MazePath {

  private int junctions;
  private List<MazeNode> nodes;

  @Override
  public String toString() {
    return "MazePath{" +
        "size=" + nodes.size() +
        "junctions=" + junctions +
        ", nodes=" + nodes +
        '}';
  }
}
