package com.ursuradu.maze.enums;

public enum MazeDrawStyle {
  PIPES(false),
  PIPES_BRIDGES(true),
  CLASSIC(false);

  private final boolean hasBridges;

  MazeDrawStyle(final boolean hasBridges) {
    this.hasBridges = hasBridges;
  }

  public boolean hasBridges() {
    return hasBridges;
  }
}
