package com.ursuradu.maze.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MazeNode {

  protected Position position;
  protected MazeNode parent;
  protected List<MazeNode> children = new ArrayList<>();
  protected boolean isEdge;

  public MazeNode(final MazeNode parent, final Position position, final boolean isEdge) {
    this.parent = parent;
    this.position = position;
    this.isEdge = isEdge;
  }

  public MazeNode(final Position position, final boolean isEdge) {
    this.position = position;
    this.isEdge = isEdge;
  }

  @Override
  public String toString() {
    return position.toString();
  }
}
